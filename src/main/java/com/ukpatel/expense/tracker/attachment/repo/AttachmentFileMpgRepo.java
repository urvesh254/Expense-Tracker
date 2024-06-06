package com.ukpatel.expense.tracker.attachment.repo;

import com.ukpatel.expense.tracker.attachment.entity.AttachmentFileMpg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentFileMpgRepo extends JpaRepository<AttachmentFileMpg, Long> {

    List<AttachmentFileMpg> findByAttachmentMstAttachmentIdAndActiveFlag(Long attachmentId, Short activeFlag);
}
