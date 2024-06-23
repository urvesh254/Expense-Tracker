package com.ukpatel.expense.tracker.auth.service;

import com.ukpatel.expense.tracker.attachment.entity.AttachmentMst;
import com.ukpatel.expense.tracker.attachment.service.AttachmentService;
import com.ukpatel.expense.tracker.auth.dto.ChangePasswordRequestDTO;
import com.ukpatel.expense.tracker.auth.dto.RegisterRequestDTO;
import com.ukpatel.expense.tracker.auth.dto.UserDTO;
import com.ukpatel.expense.tracker.auth.entity.BlacklistedJwtTxn;
import com.ukpatel.expense.tracker.auth.entity.UserDtl;
import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.auth.jwt.JwtUtils;
import com.ukpatel.expense.tracker.auth.repo.BlacklistedJwtTxnRepo;
import com.ukpatel.expense.tracker.auth.repo.UserDtlRepo;
import com.ukpatel.expense.tracker.auth.repo.UserMstRepo;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static com.ukpatel.expense.tracker.auth.constants.UserConstants.*;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.*;

@Service
@RequiredArgsConstructor
public class UserMstService {

    private final JwtUtils jwtUtils;
    private final UserMstRepo userMstRepo;
    private final UserDtlRepo userDtlRepo;
    private final PasswordEncoder passwordEncoder;
    private final AttachmentService attachmentService;
    private final BlacklistedJwtTxnRepo blacklistedJwtTxnRepo;

    public Optional<UserMst> findUserByEmail(String username) {
        return userMstRepo.findByEmail(username);
    }

    @Transactional
    public void saveUser(RegisterRequestDTO registerRequestDTO) {
        if (userMstRepo.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Email is already exists");
        }
        UserSessionInfo userSessionInfo = getUserSessionInfo();

        UserMst userMst = new UserMst();
        userMst.setEmail(registerRequestDTO.getEmail());
        userMst.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        userMst.setPwdChangeType(PWD_CHANGE_TYPE_NEW_USER);
        userMst.setActiveFlag(STATUS_ACTIVE);
        userMst.setCreatedDate(new Date());
        userMst.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        userMstRepo.save(userMst);

        UserDtl userDtl = new UserDtl();
        userDtl.setUserMst(userMst);
        userDtl.setFullName(registerRequestDTO.getFullName());
        userDtl.setDob(registerRequestDTO.getDob());
        userDtl.setProfileAttachment(getAttachmentIdForProfilePic(registerRequestDTO.getProfileImg()));
        userDtl.setActiveFlag(STATUS_ACTIVE);
        userDtl.setCreatedDate(new Date());
        userDtl.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        userDtlRepo.save(userDtl);
    }

    @Transactional
    public void logout(String jwtToken) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = new UserMst();
        loggedInUser.setUserId(userSessionInfo.getUserDTO().getUserId());

        Date validTill = jwtUtils.decodeToken(jwtToken).getExpiration();

        BlacklistedJwtTxn blacklistedJwtTxn = new BlacklistedJwtTxn();
        blacklistedJwtTxn.setToken(jwtToken);
        blacklistedJwtTxn.setValidTill(validTill);
        blacklistedJwtTxn.setActiveFlag(STATUS_ACTIVE);
        blacklistedJwtTxn.setCreatedByUser(loggedInUser);
        blacklistedJwtTxn.setCreatedDate(new Date());
        blacklistedJwtTxn.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        blacklistedJwtTxnRepo.save(blacklistedJwtTxn);
    }

    @Transactional
    public void changeUserPassword(ChangePasswordRequestDTO changePasswordRequestDTO, String jwtToken) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();

        UserMst userMst = findUserByEmail(changePasswordRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not Found!!"));

        if (!passwordEncoder.matches(changePasswordRequestDTO.getCurrentPassword(), userMst.getPassword())) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "Incorrect Password!!");
        } else if (changePasswordRequestDTO.getCurrentPassword().equals(changePasswordRequestDTO.getNewPassword())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Current password and new password should not be same!!");
        }

        String newEncodedPassword = passwordEncoder.encode(changePasswordRequestDTO.getNewPassword());
        userMst.setPassword(newEncodedPassword);
        userMst.setPwdChangeType(PWD_CHANGE_TYPE_CHANGE_PASSWORD);
        userMst.setUpdatedDate(new Date());
        userMst.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        userMstRepo.save(userMst);

        // Blacklisting the existing JWT token.
        logout(jwtToken);
    }

    @Transactional
    private AttachmentMst getAttachmentIdForProfilePic(MultipartFile file) {
        if (file == null) return null;

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) return null;

        ALLOWED_EXTENSIONS_FOR_PROFILE.stream()
                .filter(filename::endsWith)
                .findAny()
                .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, "Profile picture file must be from " + ALLOWED_EXTENSIONS_FOR_PROFILE));

        try {
            return attachmentService.saveMultipartFileAttachment(file);
        } catch (IOException e) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public UserDTO getUserInfo() {
        UserMst loggedInUser = getLoggedInUser();
        UserMst userMst = userMstRepo.findById(loggedInUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not Found!!"));
        UserDtl userDtl = userMst.getUserDtl();

        return UserDTO.builder()
                .userId(userMst.getUserId())
                .email(userMst.getEmail())
                .fullName(userDtl.getFullName())
                .dob(userDtl.getDob())
                .profileImgUrl(getProfileImgUrlFromAttachment(userDtl.getProfileAttachment()))
                .build();
    }

    private String getProfileImgUrlFromAttachment(AttachmentMst profileAttachment) {
        if (profileAttachment == null) return null;

        return String.format("/api/attachment/%d/%d?option=view",
                profileAttachment.getAttachmentId(),
                profileAttachment.getAttachmentFileMpgList().get(0).getAttachmentFileId());
    }

    @Transactional
    public UserDTO updateUserInfo(UserDTO userDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();
        UserMst userMst = userMstRepo.findById(loggedInUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not Found!!"));
        UserDtl userDtl = userMst.getUserDtl();

        String fullName = userDTO.getFullName();
        if (fullName != null && !fullName.isEmpty()) {
            userDtl.setFullName(fullName);
        }

        Date dob = userDTO.getDob();
        if (dob != null) {
            userDtl.setDob(dob);
        }

        AttachmentMst attachmentIdForProfilePic = getAttachmentIdForProfilePic(userDTO.getProfileImg());
        if (attachmentIdForProfilePic != null) {
            userDtl.setProfileAttachment(attachmentIdForProfilePic);
        }
        
        userDtl.setUpdatedDate(new Date());
        userDtl.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        userDtlRepo.save(userDtl);

        // Setting values to dto
        return UserDTO.builder()
                .userId(userMst.getUserId())
                .email(userMst.getEmail())
                .fullName(userDtl.getFullName())
                .dob(userDtl.getDob())
                .profileImgUrl(getProfileImgUrlFromAttachment(userDtl.getProfileAttachment()))
                .build();
    }
}
