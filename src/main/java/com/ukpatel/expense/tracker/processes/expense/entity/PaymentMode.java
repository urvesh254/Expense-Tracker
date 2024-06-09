package com.ukpatel.expense.tracker.processes.expense.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment_modes")
public class PaymentMode extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentModeId;

    @Column(name = "payment_mode_name", nullable = false)
    private String paymentModeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbook_id", nullable = false)
    private Cashbook cashbook;
}
