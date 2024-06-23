package com.ukpatel.expense.tracker.auth.service;

import com.ukpatel.expense.tracker.auth.dto.ChangePasswordRequestDTO;
import com.ukpatel.expense.tracker.auth.dto.RegisterRequestDTO;
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

import java.util.Date;
import java.util.Optional;

import static com.ukpatel.expense.tracker.auth.constants.UserConstants.PWD_CHANGE_TYPE_NEW_USER;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.getUserSessionInfo;

@Service
@RequiredArgsConstructor
public class UserMstService {

    private final JwtUtils jwtUtils;
    private final UserMstRepo userMstRepo;
    private final UserDtlRepo userDtlRepo;
    private final PasswordEncoder passwordEncoder;
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
        userMst.setUpdatedDate(new Date());
        userMst.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        userMstRepo.save(userMst);

        // Blacklisting the existing JWT token.
        logout(jwtToken);
    }
}
