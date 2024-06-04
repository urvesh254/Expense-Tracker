package com.ukpatel.expense.tracker.attachment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AttachmentDTO {

    private Long attachmentId;
    private Long attachmentFileId;
}
