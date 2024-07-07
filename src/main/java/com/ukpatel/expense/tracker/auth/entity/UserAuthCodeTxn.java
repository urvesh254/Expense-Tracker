package com.ukpatel.expense.tracker.auth.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import com.ukpatel.expense.tracker.mail.entity.EmailAuditTrail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_auth_code_txn")
public class UserAuthCodeTxn extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAuthCodeTxnId;

    @Column(name = "auth_code", nullable = false)
    private String authCode;

    @Column(name = "valid_till", nullable = false)
    private Date validTill;

    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @OneToOne
    @JoinColumn(name = "email_audit_id", nullable = false)
    private EmailAuditTrail emailAuditTrail;
}
