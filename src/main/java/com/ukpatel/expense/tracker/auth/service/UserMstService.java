package com.ukpatel.expense.tracker.auth.service;

import com.ukpatel.expense.tracker.attachment.entity.AttachmentMst;
import com.ukpatel.expense.tracker.attachment.service.AttachmentService;
import com.ukpatel.expense.tracker.auth.dto.*;
import com.ukpatel.expense.tracker.auth.entity.BlacklistedJwtTxn;
import com.ukpatel.expense.tracker.auth.entity.UserAuthCodeTxn;
import com.ukpatel.expense.tracker.auth.entity.UserDtl;
import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.auth.jwt.JwtUtils;
import com.ukpatel.expense.tracker.auth.jwt.property.JwtProperties;
import com.ukpatel.expense.tracker.auth.repo.BlacklistedJwtTxnRepo;
import com.ukpatel.expense.tracker.auth.repo.UserAuthCodeTxnRepo;
import com.ukpatel.expense.tracker.auth.repo.UserDtlRepo;
import com.ukpatel.expense.tracker.auth.repo.UserMstRepo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.mail.entity.EmailAuditTrail;
import com.ukpatel.expense.tracker.mail.service.MailService;
import com.ukpatel.expense.tracker.mail.service.impl.mailerSend.utility.MailerSendUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.ukpatel.expense.tracker.auth.constants.UserConstants.*;
import static com.ukpatel.expense.tracker.auth.jwt.constant.JwtTokenType.CHANGE_PWD_ACCESS_TOKEN;
import static com.ukpatel.expense.tracker.auth.utility.UserUtility.generateAuthCode;
import static com.ukpatel.expense.tracker.auth.utility.UserUtility.randomSecureString;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.*;
import static com.ukpatel.expense.tracker.mail.constant.MailConstants.KEY_EMAIL_AUDIT_ID;
import static com.ukpatel.expense.tracker.mail.constant.MailConstants.KEY_RES_ERROR;

@Service
@RequiredArgsConstructor
public class UserMstService {

    private final JwtUtils jwtUtils;
    private final UserMstRepo userMstRepo;
    private final UserDtlRepo userDtlRepo;
    private final PasswordEncoder passwordEncoder;
    private final AttachmentService attachmentService;
    private final BlacklistedJwtTxnRepo blacklistedJwtTxnRepo;
    private final MailService mailService;
    private final MailerSendUtility mailerSendUtility;
    private final JwtProperties jwtProperties;
    private final UserAuthCodeTxnRepo userAuthCodeTxnRepo;

    public Optional<UserMst> findUserByEmail(String username) {
        return userMstRepo.findByEmail(username);
    }

    @Transactional
    public void saveUser(RegisterRequestDTO registerRequestDTO) {
        if (userMstRepo.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Email is already exists");
        }

        UserMst userMst = new UserMst();
        userMst.setEmail(registerRequestDTO.getEmail());
        userMst.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        userMst.setPwdChangeType(PWD_CHANGE_TYPE_NEW_USER);
        userMstRepo.save(userMst);

        UserDtl userDtl = new UserDtl();
        userDtl.setUserMst(userMst);
        userDtl.setFullName(registerRequestDTO.getFullName());
        userDtl.setDob(registerRequestDTO.getDob());
        userDtl.setProfileAttachment(getAttachmentIdForProfilePic(registerRequestDTO.getProfileImg()));
        userDtlRepo.save(userDtl);
    }

    @Transactional
    public void logout(String jwtToken) {
        Date validTill = jwtUtils.decodeToken(jwtToken).getExpiration();

        BlacklistedJwtTxn blacklistedJwtTxn = new BlacklistedJwtTxn();
        blacklistedJwtTxn.setToken(jwtToken);
        blacklistedJwtTxn.setValidTill(validTill);
        blacklistedJwtTxnRepo.save(blacklistedJwtTxn);
    }

    @Transactional
    public void changeUserPassword(ChangePasswordRequestDTO changePasswordRequestDTO, String jwtToken) {
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

    @Transactional
    public Map<String, Object> generateAuthCodeFromEmail(String email) {
        // Validate User
        UserMst userMst = findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found!!"));

        if (userAuthCodeTxnRepo.isForgotPasswordAttemptsExhausted(userMst.getUserId(), FORGOT_PASSWORD_ATTEMPT_MAX_COUNT)) {
            throw new ApplicationException(HttpStatus.TOO_MANY_REQUESTS,
                    "You have exceeded the maximum number of password reset attempts for today. Please try again tomorrow");
        }

        // Setting current user in user session info
        getUserSessionInfo().getUserDTO().setUserId(userMst.getUserId());

        String authCode = generateAuthCode(AUTH_CODE_LENGTH);
        String verificationCode = randomSecureString(VERIFICATION_CODE_LENGTH);
        EmailAuditTrail emailAuditTrail = new EmailAuditTrail();

        // Sending Mail
        try {
            Map<String, Object> mailResObj = mailService.sendMail(mailerSendUtility.getAuthenticationCodeMailArgs(
                    userMst.getUserDtl().getFullName(),
                    userMst.getEmail(),
                    authCode
            ));

            Optional.ofNullable(mailResObj.get(KEY_RES_ERROR))
                    .ifPresent(errorMsg -> {
                        throw new ApplicationException(errorMsg.toString());
                    });

            Long emailAuditId = Long.valueOf(mailResObj.get(KEY_EMAIL_AUDIT_ID).toString());
            emailAuditTrail.setEmailAuditId(emailAuditId);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }

        // Making entry in UserAuthCodeTxn
        UserAuthCodeTxn userAuthCodeTxn = new UserAuthCodeTxn();
        userAuthCodeTxn.setAuthCode(authCode);
        userAuthCodeTxn.setVerificationCode(verificationCode);
        userAuthCodeTxn.setValidTill(new Date(System.currentTimeMillis() + jwtProperties.getChangePwdTokenValidity()));
        userAuthCodeTxn.setEmailAuditTrail(emailAuditTrail);
        userAuthCodeTxnRepo.save(userAuthCodeTxn);

        Map<String, Object> resBody = new HashMap<>();
        resBody.put(KEY_VERIFICATION_CODE, verificationCode);
        resBody.put(KEY_VALID_TILL, userAuthCodeTxn.getValidTill());
        return resBody;
    }

    @Transactional
    public TokenResponseDTO verifyAuthCode(VerifyAuthCodeDTO verifyAuthCodeDTO) {
        // Validate authCode
        UserAuthCodeTxn userAuthCodeTxn = userAuthCodeTxnRepo.findByAuthCodeAndVerificationCodeAndActiveFlag(
                verifyAuthCodeDTO.getAuthCode(),
                verifyAuthCodeDTO.getVerificationCode(),
                STATUS_ACTIVE
        ).orElseThrow(() -> new ApplicationException(HttpStatus.UNAUTHORIZED, "Invalid authentication code or verification code."));

        if (userAuthCodeTxn.getValidTill().before(new Date())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Authentication code has expired. Please request a new one");
        }

        // Generating reset token
        UserMst userMst = userAuthCodeTxn.getCreatedByUser();
        String jwtToken = jwtUtils.issueToken(userMst.getUserId(), CHANGE_PWD_ACCESS_TOKEN);

        // Setting current user in user session info
        getUserSessionInfo().getUserDTO().setUserId(userMst.getUserId());

        // deactivating verified authCode entry
        userAuthCodeTxn.setActiveFlag(STATUS_INACTIVE);
        userAuthCodeTxnRepo.save(userAuthCodeTxn);

        return TokenResponseDTO.builder()
                .email(userMst.getEmail())
                .userId(userMst.getUserId())
                .token(jwtToken)
                .build();
    }

    @Transactional
    public void handleForgotPasswordRequest(ForgotPasswordRequestDTO forgotPasswordRequestDTO, String resetToken) {
        UserMst userMst = userMstRepo.findById(getUserInfo().getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not Found!!"));

        // validate new password
        String newPassword = forgotPasswordRequestDTO.getNewPassword();
        String confirmNewPassword = forgotPasswordRequestDTO.getConfirmNewPassword();
        if (!newPassword.equals(confirmNewPassword)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Passwords do not match. Please ensure newPassword and confirmPassword are the same.");
        } else if (passwordEncoder.matches(newPassword, userMst.getPassword())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "New password must be different from the current password. Please choose a different password.");
        }

        // Set new password
        String newEncodedPassword = passwordEncoder.encode(newPassword);
        userMst.setPassword(newEncodedPassword);
        userMst.setPwdChangeType(PWD_CHANGE_TYPE_FORGOT_PASSWORD);
        userMstRepo.save(userMst);

        // logout
        logout(resetToken);
    }
}
