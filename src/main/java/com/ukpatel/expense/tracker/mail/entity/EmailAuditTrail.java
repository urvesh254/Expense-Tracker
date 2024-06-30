package com.ukpatel.expense.tracker.mail.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "email_audit_trail")
public class EmailAuditTrail extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailAuditId;

    @Column(name = "request_data", nullable = false)
    private String requestData;

    @Column(name = "response_data", nullable = false)
    private String responseData;
}
