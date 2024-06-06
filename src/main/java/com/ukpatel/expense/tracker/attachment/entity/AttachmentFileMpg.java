package com.ukpatel.expense.tracker.attachment.entity;

import com.ukpatel.expense.tracker.common.base.entity.AuditBaseWithUpdater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attachment_file_mpg")
public class AttachmentFileMpg extends AuditBaseWithUpdater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentFileId;

    @ManyToOne
    @JoinColumn(name = "attachment_id", nullable = false)
    private AttachmentMst attachmentMst;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_desc", nullable = false)
    private String fileDesc;

    @Column(name = "file_size_in_bytes", nullable = false)
    private Long fileSizeInBytes;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_data", nullable = false)
    private byte[] fileData;
}
