package com.ukpatel.expense.tracker.attachment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AttachmentFileMpgDTO {

    private Long attachmentId;
    private Long attachmentFileId;
    private String fileName;
    private String fileDesc;
    private Long fileSizeInBytes;
    private String contentType;

    @JsonIgnore
    private byte[] fileData;
}
