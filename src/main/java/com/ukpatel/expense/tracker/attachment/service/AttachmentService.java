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
import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
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
import static com.ukpatel.expense.tracker.attachment.service.AttachmentSessionManagementService.generateNewSessionId;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.getUserSessionInfo;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentOperationTxnRepo operationTxnRepo;
    private final AttachmentMstRepo attachmentMstRepo;
    private final AttachmentFileMpgRepo attachmentFileMpgRepo;

    @Transactional
    public AttachmentDTO saveFileAttachmentInSession(AttachmentDTO attachmentDTO, MultipartFile file) throws IOException {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = new UserMst();
        loggedInUser.setUserId(userSessionInfo.getUserDTO().getUserId());

        // Configuring Attachment Mst
        AttachmentMst attachmentMst;
        if (attachmentDTO.getAttachmentId() != null) {
            attachmentMst = attachmentMstRepo.findById(attachmentDTO.getAttachmentId()).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "attachmentId not found"));
        } else {
            attachmentMst = new AttachmentMst();
            attachmentMst.setActiveFlag(STATUS_ACTIVE);
            attachmentMst.setCreatedByUser(loggedInUser);
            attachmentMst.setCreatedDate(new Date());
            attachmentMst.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
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
        attachmentFileMpg.setCreatedByUser(loggedInUser);
        attachmentFileMpg.setCreatedDate(new Date());
        attachmentFileMpg.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        attachmentFileMpgRepo.save(attachmentFileMpg);

        // Inserting file operation in session
        UUID sessionId = generateNewSessionId(attachmentDTO.getSessionId());
        AttachmentOperationTxn operationTxn = new AttachmentOperationTxn(sessionId, attachmentFileMpg, OperationType.INSERT);
        operationTxn.setCreatedByUser(loggedInUser);
        operationTxn.setCreatedDate(new Date());
        operationTxn.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        operationTxnRepo.save(operationTxn);

        // Setting data that need to send as response
        attachmentDTO.setAttachmentId(attachmentMst.getAttachmentId());
        attachmentDTO.setAttachmentFileId(attachmentFileMpg.getAttachmentFileId());
        attachmentDTO.setSessionId(sessionId);

        return attachmentDTO;
    }

    @Transactional
    public AttachmentDTO deleteFileAttachmentInSession(AttachmentDTO attachmentDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = new UserMst();
        loggedInUser.setUserId(userSessionInfo.getUserDTO().getUserId());

        if (attachmentDTO.getAttachmentId() == null || attachmentDTO.getAttachmentFileId() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Attachment ID and Attachment File ID must not be null");
        }

        AttachmentFileMpg attachmentFileMpg = attachmentFileMpgRepo.findById(attachmentDTO.getAttachmentFileId()).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "attachmentFileId not found"));
        if (!attachmentFileMpg.getAttachmentMst().getAttachmentId().equals(attachmentDTO.getAttachmentId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Incorrect combination of Attachment ID and Attachment File ID");
        }

        // Inserting file operation in session
        UUID sessionId = generateNewSessionId(attachmentDTO.getSessionId());
        AttachmentOperationTxn operationTxn = new AttachmentOperationTxn(sessionId, attachmentFileMpg, OperationType.DELETE);
        operationTxn.setCreatedByUser(loggedInUser);
        operationTxn.setCreatedDate(new Date());
        operationTxn.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
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
        for (AttachmentOperationTxn operationTxn : attachmentOperationList) {
            AttachmentFileMpg attachmentFileMpg = operationTxn.getAttachmentFileMpg();
            Long attachmentFileId = attachmentFileMpg.getAttachmentFileId();

            short activeFlag = INACTIVE.getStatus();
            if (operationTxn.getOperationType() == OperationType.INSERT) {
                activeFlag = 1;
                attachmentsCreatedInSession.add(attachmentFileId);
            }

            attachmentFileMpg.setActiveFlag(activeFlag);
            attachmentFileMpgRepo.save(attachmentFileMpg);
        }

        // 3. Delete session operation from table attachment_operation_txn
        // If your business logic didn't allow you to delete then you can keep it with active_flag = 0
        operationTxnRepo.deleteBySessionId(attachmentDTO.getSessionId());

        // If file stored as created status then it is temporary file, so we can delete it
        attachmentsCreatedInSession.forEach(attachmentFileMpgRepo::deleteById);
    }
}
