package com.ukpatel.expense.tracker.auth.repo;

import com.ukpatel.expense.tracker.auth.entity.UserAuthCodeTxn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthCodeTxnRepo extends JpaRepository<UserAuthCodeTxn, Long> {

    Optional<UserAuthCodeTxn> findByAuthCodeAndVerificationCodeAndActiveFlag(String authCode, String verificationCode, Short activeFlag);

    @Query(value = """
            SELECT
            	CASE
            		WHEN count(*)>= :maxCount THEN TRUE
            		ELSE FALSE
            	END
            FROM
            	user_auth_code_txn
            WHERE
            	created_by_user_id = :userId
            	AND DATE(created_date) = current_date;
            """, nativeQuery = true)
    boolean isForgotPasswordAttemptsExhausted(Long userId, Integer maxCount);
}