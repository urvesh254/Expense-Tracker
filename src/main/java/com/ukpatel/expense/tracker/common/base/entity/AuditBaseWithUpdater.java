package com.ukpatel.expense.tracker.common.base.entity;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.getLoggedInUser;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.getUserSessionInfo;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditBaseWithUpdater extends AuditBase {

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    protected UserMst updatedByUser;

    @Column(name = "updated_date")
    protected Date updatedDate;

    @Column(name = "updated_by_ip")
    protected String updatedByIp;

    @PreUpdate
    private void handleBeforeUpdate() {
        UserSessionInfo userSessionInfo = getUserSessionInfo();

        updatedByUser = getLoggedInUser();
        updatedDate = new Date();
        updatedByIp = userSessionInfo.getRemoteIpAddr();
    }
}
