package com.ukpatel.expense.tracker.attachment.service;

import com.ukpatel.expense.tracker.attachment.constants.OperationType;
import com.ukpatel.expense.tracker.attachment.dto.AttachmentDTO;
import com.ukpatel.expense.tracker.attachment.dto.AttachmentFileMpgDTO;
import com.ukpatel.expense.tracker.attachment.entity.AttachmentFileMpg;
import com.ukpatel.expense.tracker.attachment.entity.AttachmentMst;
import com.ukpatel.expense.tracker.attachment.entity.AttachmentOperationTxn;
import com.ukpatel.expense.tracker.attachment.repo.AttachmentFileMpgRepo;
import com.ukpatel.expense.tracker.attachment.repo.AttachmentMstRepo;
import com.ukpatel.expense.tracker.attachment.repo.AttachmentOperationTxnRepo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.ukpatel.expense.tracker.attachment.constants.AttachmentStatus.CREATED;
import static com.ukpatel.expense.tracker.attachment.constants.AttachmentStatus.INACTIVE;
import static com.ukpatel.expense.tracker.attachment.constants.OperationType.DELETE;
import static com.ukpatel.expense.tracker.attachment.constants.OperationType.INSERT;
import static com.ukpatel.expense.tracker.attachment.service.AttachmentSessionManagementService.generateNewSessionId;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentOperationTxnRepo operationTxnRepo;
    private final AttachmentMstRepo attachmentMstRepo;
    private final AttachmentFileMpgRepo attachmentFileMpgRepo;

    @Transactional
    public AttachmentDTO saveFileAttachmentInSession(AttachmentDTO attachmentDTO, MultipartFile file) throws IOException {
        // Configuring Attachment Mst
        AttachmentMst attachmentMst;
        if (attachmentDTO.getAttachmentId() != null) {
            attachmentMst = attachmentMstRepo.findById(attachmentDTO.getAttachmentId())
                    .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "attachmentId not found"));
        } else {
            attachmentMst = new AttachmentMst();
            attachmentMstRepo.save(attachmentMst);
        }

        // Saving file in Attachment File Mpg Table
        AttachmentFileMpg attachmentFileMpg = new AttachmentFileMpg();
        attachmentFileMpg.setAttachmentMst(attachmentMst);
        attachmentFileMpg.setFileName(file.getOriginalFilename());
        attachmentFileMpg.setFileDesc(file.getOriginalFilename());
        attachmentFileMpg.setFileSizeInBytes(file.getSize());
        attachmentFileMpg.setContentType(file.getContentType());
        attachmentFileMpg.setFileData(file.getBytes());
        attachmentFileMpg.setActiveFlag(CREATED.getStatus());
        attachmentFileMpgRepo.save(attachmentFileMpg);

        // Inserting file operation in session
        UUID sessionId = generateNewSessionId(attachmentDTO.getSessionId());
        AttachmentOperationTxn operationTxn = new AttachmentOperationTxn(sessionId, attachmentFileMpg, INSERT);
        operationTxnRepo.save(operationTxn);

        // Setting data that need to send as response
        attachmentDTO.setAttachmentId(attachmentMst.getAttachmentId());
        attachmentDTO.setAttachmentFileId(attachmentFileMpg.getAttachmentFileId());
        attachmentDTO.setSessionId(sessionId);

        return attachmentDTO;
    }

    @Transactional
    public AttachmentDTO deleteFileAttachmentInSession(AttachmentDTO attachmentDTO) {
        if (attachmentDTO.getAttachmentId() == null || attachmentDTO.getAttachmentFileId() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Attachment ID and Attachment File ID must not be null");
        }

        AttachmentFileMpg attachmentFileMpg = attachmentFileMpgRepo.findById(attachmentDTO.getAttachmentFileId()).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "attachmentFileId not found"));
        if (!attachmentFileMpg.getAttachmentMst().getAttachmentId().equals(attachmentDTO.getAttachmentId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Incorrect combination of Attachment ID and Attachment File ID");
        }

        // Inserting file operation in session
        UUID sessionId = generateNewSessionId(attachmentDTO.getSessionId());
        AttachmentOperationTxn operationTxn = new AttachmentOperationTxn(sessionId, attachmentFileMpg, DELETE);
        operationTxnRepo.save(operationTxn);

        // Setting data that need to send as response
        attachmentDTO.setAttachmentId(attachmentFileMpg.getAttachmentMst().getAttachmentId());
        attachmentDTO.setAttachmentFileId(attachmentFileMpg.getAttachmentFileId());
        attachmentDTO.setSessionId(sessionId);

        return attachmentDTO;
    }

    @Transactional
    public List<AttachmentFileMpgDTO> getAllFileAttachments(Long attachmentId) {
        if (attachmentId == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Attachment ID must not be null");
        }

        attachmentMstRepo.findById(attachmentId).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "attachmentId not found"));
        List<AttachmentFileMpg> allActiveAttachments = attachmentFileMpgRepo.findByAttachmentMstAttachmentIdAndActiveFlag(attachmentId, STATUS_ACTIVE);

        return allActiveAttachments.stream()
                .map(attachmentFileMpg -> AttachmentFileMpgDTO.builder()
                        .attachmentId(attachmentId)
                        .attachmentFileId(attachmentFileMpg.getAttachmentFileId())
                        .fileName(attachmentFileMpg.getFileName())
                        .fileDesc(attachmentFileMpg.getFileDesc())
                        .fileSizeInBytes(attachmentFileMpg.getFileSizeInBytes())
                        .contentType(attachmentFileMpg.getContentType())
                        .build())
                .toList();
    }

    @Transactional
    public AttachmentFileMpgDTO getFileAttachments(Long attachmentId, Long attachmentFileId) {

        if (attachmentId == null || attachmentFileId == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Attachment ID and Attachment File ID must not be null");
        }

        AttachmentFileMpg attachmentFileMpg = attachmentFileMpgRepo.findById(attachmentFileId).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "attachmentFileId not found"));
        if (!attachmentFileMpg.getAttachmentMst().getAttachmentId().equals(attachmentId)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Incorrect combination of Attachment ID and Attachment File ID");
        }

        return AttachmentFileMpgDTO.builder()
                .attachmentId(attachmentId)
                .attachmentFileId(attachmentFileMpg.getAttachmentFileId())
                .fileName(attachmentFileMpg.getFileName())
                .fileDesc(attachmentFileMpg.getFileDesc())
                .fileSizeInBytes(attachmentFileMpg.getFileSizeInBytes())
                .contentType(attachmentFileMpg.getContentType())
                .fileData(attachmentFileMpg.getFileData())
                .build();
    }

    @Transactional
    public void saveFileAttachments(AttachmentDTO attachmentDTO) {
        // Validations
        if (attachmentDTO.getSessionId() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Attachment SessionId is missing");
        }

        // 1. Retrieve all attachments in session
        List<AttachmentOperationTxn> attachmentOperationList = operationTxnRepo.findBySessionIdOrderByCreatedDate(attachmentDTO.getSessionId());

        // 2. Iterating over all and make changes according to db.
        Set<Long> attachmentsCreatedInSession = new HashSet<>();
        Set<Long> attachmentDeletedInSession = new HashSet<>();
        for (AttachmentOperationTxn operationTxn : attachmentOperationList) {
            AttachmentFileMpg attachmentFileMpg = operationTxn.getAttachmentFileMpg();
            Long attachmentFileId = attachmentFileMpg.getAttachmentFileId();
            OperationType operationType = operationTxn.getOperationType();

            short activeFlag = INACTIVE.getStatus();
            if (operationType == INSERT) {
                activeFlag = 1;
                attachmentsCreatedInSession.add(attachmentFileId);
            } else if (attachmentsCreatedInSession.contains(attachmentFileId)
                    && operationType == DELETE) {
                attachmentDeletedInSession.add(attachmentFileId);
            }

            attachmentFileMpg.setActiveFlag(activeFlag);
            attachmentFileMpgRepo.save(attachmentFileMpg);
        }

        // 3. Delete session operation from table attachment_operation_txn
        // Cleanup Start
        // If your business logic didn't allow you to delete then you can keep it with active_flag = 0
        operationTxnRepo.deleteBySessionId(attachmentDTO.getSessionId());

        // If file stored as created status then it is temporary file, so we can delete it
        attachmentDeletedInSession.forEach(attachmentFileMpgRepo::deleteById);

        // Cleanup attachmentId which are created for storing temporary files.
        attachmentMstRepo.deleteAllUnusedAttachments();
        // Cleanup End
    }

    public Optional<AttachmentMst> findById(Long attachmentId) {
        return attachmentMstRepo.findById(attachmentId);
    }

    @Transactional
    public AttachmentMst saveMultipartFileAttachment(MultipartFile... files) throws IOException {
        if (files.length == 0) return null;
        return saveMultipartFileAttachment(Arrays.asList(files));
    }

    @Transactional
    public AttachmentMst saveMultipartFileAttachment(List<MultipartFile> files) throws IOException {
        // Remove all null objects and after if no file objects will there then returning null attachmentId
        files = getValidFileList(files);
        if (files.isEmpty()) {
            return null;
        }

        // Configuring Attachment Mst
        AttachmentMst attachmentMst = new AttachmentMst();
        attachmentMstRepo.save(attachmentMst);

        // Saving files in Attachment File Mpg Table
        List<AttachmentFileMpg> attachmentFileMpgList = new ArrayList<>();
        for (MultipartFile file : files) {
            AttachmentFileMpg attachmentFileMpg = new AttachmentFileMpg();
            attachmentFileMpg.setAttachmentMst(attachmentMst);
            attachmentFileMpg.setFileName(file.getOriginalFilename());
            attachmentFileMpg.setFileDesc(file.getOriginalFilename());
            attachmentFileMpg.setFileSizeInBytes(file.getSize());
            attachmentFileMpg.setContentType(file.getContentType());
            attachmentFileMpg.setFileData(file.getBytes());

            attachmentFileMpgRepo.save(attachmentFileMpg);
            attachmentFileMpgList.add(attachmentFileMpg);
        }

        attachmentMst.setAttachmentFileMpgList(attachmentFileMpgList);
        return attachmentMst;
    }

    private List<MultipartFile> getValidFileList(List<MultipartFile> files) {
        return files.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
