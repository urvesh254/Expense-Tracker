package com.ukpatel.expense.tracker.processes.expense.entity;

import com.ukpatel.expense.tracker.attachment.entity.AttachmentMst;
import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.category.entity.Category;
import com.ukpatel.expense.tracker.processes.expense.constant.EntryType;
import com.ukpatel.expense.tracker.processes.paymentMode.entity.PaymentMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "expenses")
public class Expense extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private EntryType entryType;

    @Column(name = "entry_date_time", nullable = false)
    private Date entryDateTime;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "remarks")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_mode_id")
    private PaymentMode paymentMode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id")
    private AttachmentMst attachmentMst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbook_id", nullable = false)
    private Cashbook cashbook;
}
