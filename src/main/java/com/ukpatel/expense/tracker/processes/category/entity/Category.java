package com.ukpatel.expense.tracker.processes.category.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbook_id", nullable = false)
    private Cashbook cashbook;
}
