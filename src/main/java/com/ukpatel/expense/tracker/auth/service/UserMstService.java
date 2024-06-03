package com.ukpatel.expense.tracker.auth.service;

import com.ukpatel.expense.tracker.auth.dto.RegisterRequestDTO;
import com.ukpatel.expense.tracker.auth.entity.UserDtl;
import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.auth.repo.UserDtlRepo;
import com.ukpatel.expense.tracker.auth.repo.UserMstRepo;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class UserMstService implements UserDetailsService {

    private final UserMstRepo userMstRepo;
    private final UserDtlRepo userDtlRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserMst> userMstOptional = findUserByEmail(username);

        if (userMstOptional.isPresent()) {
            return userMstOptional.get();
        } else {
            throw new UsernameNotFoundException("User not Found!!");
        }
    }

    public Optional<UserMst> findUserByEmail(String username) {
        return userMstRepo.findByEmail(username);
    }

    @Transactional
    public void saveUser(RegisterRequestDTO registerRequestDTO) {
        if (userMstRepo.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Email is already exists");
        }
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
}