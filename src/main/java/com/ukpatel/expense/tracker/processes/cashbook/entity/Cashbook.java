package com.ukpatel.expense.tracker.processes.cashbook.entity;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cashbooks")
public class Cashbook extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cashbookId;

    @Column(name = "cashbook_name", nullable = false)
    private String cashbookName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserMst userMst;
}
