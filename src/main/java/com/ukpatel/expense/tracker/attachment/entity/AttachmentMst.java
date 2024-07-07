package com.ukpatel.expense.tracker.attachment.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "attachment_mst")
public class AttachmentMst extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attachmentMst")
    private List<AttachmentFileMpg> attachmentFileMpgList;
}
