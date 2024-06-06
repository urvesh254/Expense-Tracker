package com.ukpatel.expense.tracker.attachment.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attachment_mst")
public class AttachmentMst extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;
}
