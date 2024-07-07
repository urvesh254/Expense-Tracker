package com.ukpatel.expense.tracker.common.base.entity;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.*;

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

    @PrePersist
    private void handleBeforePersist() {
        UserSessionInfo userSessionInfo = getUserSessionInfo();

        if (activeFlag == null) {
            activeFlag = STATUS_ACTIVE;
        }
        createdByUser = getLoggedInUser();
        createdDate = new Date();
        createdByIp = userSessionInfo.getRemoteIpAddr();
    }
}
