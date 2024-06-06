package com.ukpatel.expense.tracker.attachment.entity;

import com.ukpatel.expense.tracker.attachment.constants.OperationType;
import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "attachment_operation_txn")
@NoArgsConstructor
public class AttachmentOperationTxn extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationId;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @ManyToOne
    @JoinColumn(name = "attachment_file_id", nullable = false)
    private AttachmentFileMpg attachmentFileMpg;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    public AttachmentOperationTxn(UUID sessionId, AttachmentFileMpg attachmentFileMpg, OperationType operationType) {
        this.sessionId = sessionId;
        this.attachmentFileMpg = attachmentFileMpg;
        this.operationType = operationType;
        this.activeFlag = 1;
    }
}
