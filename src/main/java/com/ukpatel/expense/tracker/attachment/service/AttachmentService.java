package com.ukpatel.expense.tracker.attachment.service;

import com.ukpatel.expense.tracker.attachment.dto.AttachmentDTO;
import com.ukpatel.expense.tracker.attachment.dto.AttachmentFileMpgDTO;
import com.ukpatel.expense.tracker.attachment.entity.AttachmentFileMpg;
import com.ukpatel.expense.tracker.attachment.entity.AttachmentMst;
import com.ukpatel.expense.tracker.attachment.repo.AttachmentFileMpgRepo;
import com.ukpatel.expense.tracker.attachment.repo.AttachmentMstRepo;
import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.ukpatel.expense.tracker.attachment.constants.AttachmentConstants.CREATED;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.*;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentMstRepo attachmentMstRepo;
    private final AttachmentFileMpgRepo attachmentFileMpgRepo;

    @Transactional
    public AttachmentDTO saveAttachment(AttachmentDTO attachmentDTO, MultipartFile file) throws IOException {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst createdByUser = new UserMst();
        createdByUser.setUserId(userSessionInfo.getUserDTO().getUserId());

        // Configuring Attachment Mst
        AttachmentMst attachmentMst;
        if (attachmentDTO.getAttachmentId() != null) {
            attachmentMst = attachmentMstRepo.findById(attachmentDTO.getAttachmentId()).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "attachmentId not found"));
        } else {
            attachmentMst = new AttachmentMst();
            attachmentMst.setActiveFlag(STATUS_ACTIVE);
            attachmentMst.setCreatedByUser(createdByUser);
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

        attachmentFileMpg.setSavedFlag(CREATED);
        attachmentFileMpg.setActiveFlag(STATUS_ACTIVE);
        attachmentFileMpg.setCreatedByUser(createdByUser);
        attachmentFileMpg.setCreatedDate(new Date());
        attachmentFileMpg.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        attachmentFileMpgRepo.save(attachmentFileMpg);

        // Setting data that need to send as response
        attachmentDTO.setAttachmentId(attachmentMst.getAttachmentId());
        attachmentDTO.setAttachmentFileId(attachmentFileMpg.getAttachmentFileId());

        return attachmentDTO;
    }

    public AttachmentDTO updateAttachment(AttachmentDTO attachmentDTO, MultipartFile file) throws IOException {
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

        attachmentFileMpg.setFileName(file.getOriginalFilename());
        attachmentFileMpg.setFileDesc(file.getOriginalFilename());
        attachmentFileMpg.setFileSizeInBytes(file.getSize());
        attachmentFileMpg.setContentType(file.getContentType());
        attachmentFileMpg.setFileData(file.getBytes());

        attachmentFileMpg.setSavedFlag(CREATED); // TODO: need to think logic again
        attachmentFileMpg.setUpdatedByUser(loggedInUser);
        attachmentFileMpg.setUpdatedDate(new Date());
        attachmentFileMpg.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        attachmentFileMpgRepo.save(attachmentFileMpg);

        return attachmentDTO;
    }

    @Transactional
    public void deleteFileAttachment(AttachmentDTO attachmentDTO) {
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

        attachmentFileMpg.setActiveFlag(STATUS_INACTIVE);
        attachmentFileMpg.setUpdatedByUser(loggedInUser);
        attachmentFileMpg.setUpdatedDate(new Date());
        attachmentFileMpg.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        attachmentFileMpgRepo.save(attachmentFileMpg);
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
}
