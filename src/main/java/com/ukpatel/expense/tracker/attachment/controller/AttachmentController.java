package com.ukpatel.expense.tracker.attachment.controller;

import com.ukpatel.expense.tracker.attachment.dto.AttachmentDTO;
import com.ukpatel.expense.tracker.attachment.dto.AttachmentFileMpgDTO;
import com.ukpatel.expense.tracker.attachment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.ukpatel.expense.tracker.attachment.constants.AttachmentConstants.OPTION_DOWNLOAD;
import static com.ukpatel.expense.tracker.attachment.constants.AttachmentConstants.OPTION_VIEW;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/")
    public ResponseEntity<AttachmentDTO> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "attachmentId", required = false) Long attachmentId,
            @RequestParam(value = "sessionId", required = false) UUID sessionId
    ) throws IOException {
        AttachmentDTO attachmentDTO = AttachmentDTO.builder()
                .attachmentId(attachmentId)
                .sessionId(sessionId)
                .build();
        attachmentDTO = attachmentService.saveFileAttachmentInSession(attachmentDTO, file);
        return new ResponseEntity<>(attachmentDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{attachmentId}/{attachmentFileId}")
    public ResponseEntity<Object> deleteFileAttachment(
            @PathVariable(value = "attachmentId") Long attachmentId,
            @PathVariable(value = "attachmentFileId") Long attachmentFileId,
            @RequestParam(value = "sessionId") UUID sessionId
    ) {
        AttachmentDTO attachmentDTO = AttachmentDTO.builder()
                .attachmentId(attachmentId)
                .attachmentFileId(attachmentFileId)
                .sessionId(sessionId)
                .build();
        attachmentDTO = attachmentService.deleteFileAttachmentInSession(attachmentDTO);
        return ResponseEntity.ok(attachmentDTO);
    }


    @GetMapping("/{attachmentId}")
    public ResponseEntity<List<AttachmentFileMpgDTO>> getAllFileAttachments(
            @PathVariable(value = "attachmentId") Long attachmentId
    ) {
        List<AttachmentFileMpgDTO> allFileAttachments = attachmentService.getAllFileAttachments(attachmentId);
        return ResponseEntity.ok(allFileAttachments);
    }

    @GetMapping("/{attachmentId}/{attachmentFileId}")
    public ResponseEntity<?> getSingleFileAttachments(
            @PathVariable(value = "attachmentId") Long attachmentId,
            @PathVariable(value = "attachmentFileId") Long attachmentFileId,
            @RequestParam(name = "option", required = false, defaultValue = "") String option
    ) {
        AttachmentFileMpgDTO attachmentFileMpgDTO = attachmentService.getFileAttachments(attachmentId, attachmentFileId);

        if (OPTION_VIEW.equalsIgnoreCase(option)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(attachmentFileMpgDTO.getContentType()))
                    .body(attachmentFileMpgDTO.getFileData());
        } else if (OPTION_DOWNLOAD.equalsIgnoreCase(option)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentFileMpgDTO.getFileName() + "\"")
                    .body(attachmentFileMpgDTO.getFileData());
        } else {
            return ResponseEntity.ok(attachmentFileMpgDTO);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveAttachmentsInDB(@RequestBody AttachmentDTO attachmentDTO) {
        attachmentService.saveFileAttachments(attachmentDTO);
        return ResponseEntity.ok().build();
    }
}
