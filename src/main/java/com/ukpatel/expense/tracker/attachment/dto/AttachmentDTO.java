package com.ukpatel.expense.tracker.attachment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class AttachmentDTO {

    @JsonIgnore
    private String attachmentName;
    private Long attachmentId;
    private Long attachmentFileId;
    private UUID sessionId;
}
