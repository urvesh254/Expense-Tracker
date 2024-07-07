package com.ukpatel.expense.tracker.common.base.entity;

import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.getUserSessionInfo;

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


    @PrePersist
    private void handleBeforePersist() {
        UserSessionInfo userSessionInfo = getUserSessionInfo();

        if (activeFlag == null) {
            activeFlag = STATUS_ACTIVE;
        }
        createdDate = new Date();
        createdByIp = userSessionInfo.getRemoteIpAddr();
    }

    @PreUpdate
    private void handleBeforeUpdate() {
        UserSessionInfo userSessionInfo = getUserSessionInfo();

        updatedDate = new Date();
        updatedByIp = userSessionInfo.getRemoteIpAddr();
    }
}
