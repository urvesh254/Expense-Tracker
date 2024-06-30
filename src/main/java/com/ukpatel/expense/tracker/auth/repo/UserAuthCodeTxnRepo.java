package com.ukpatel.expense.tracker.auth.repo;

import com.ukpatel.expense.tracker.auth.entity.UserAuthCodeTxn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthCodeTxnRepo extends JpaRepository<UserAuthCodeTxn, Long> {

    Optional<UserAuthCodeTxn> findByAuthCodeAndVerificationCodeAndActiveFlag(String authCode, String verificationCode, Short activeFlag);
}