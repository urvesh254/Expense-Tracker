package com.ukpatel.expense.tracker.auth.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "blacklisted_jwt_txn")
public class BlacklistedJwtTxn extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blacklistedJwtTxnId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "valid_till", nullable = false)
    private Date validTill;
}
