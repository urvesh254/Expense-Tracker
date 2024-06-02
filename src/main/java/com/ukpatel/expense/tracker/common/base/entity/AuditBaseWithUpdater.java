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
    private UserMst updatedByUser;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by_ip")
    private String updatedByIp;
}
