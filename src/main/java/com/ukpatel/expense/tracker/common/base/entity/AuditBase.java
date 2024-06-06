package com.ukpatel.expense.tracker.common.base.entity;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditBase {

    @Column(name = "active_flag", nullable = false)
    protected Short activeFlag;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    protected UserMst createdByUser;

    @Column(name = "created_date", nullable = false)
    protected Date createdDate;

    @Column(name = "created_by_ip", nullable = false)
    protected String createdByIp;
}
