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
public abstract class AuditBaseWithUpdater extends AuditBase {

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    protected UserMst updatedByUser;

    @Column(name = "updated_date")
    protected Date updatedDate;

    @Column(name = "updated_by_ip")
    protected String updatedByIp;
}
