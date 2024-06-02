package com.ukpatel.expense.tracker.common.base.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class TimestampedBase {

    @Column(name = "active_flag", nullable = false)
    private Short activeFlag;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "created_by_ip", nullable = false)
    private String createdByIp;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by_ip")
    private String updatedByIp;
}
