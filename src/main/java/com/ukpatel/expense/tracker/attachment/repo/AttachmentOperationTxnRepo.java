package com.ukpatel.expense.tracker.attachment.repo;

import com.ukpatel.expense.tracker.attachment.entity.AttachmentOperationTxn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentOperationTxnRepo extends JpaRepository<AttachmentOperationTxn, Long> {

    List<AttachmentOperationTxn> findBySessionIdOrderByCreatedDate(UUID attachmentFileMpg);

    @Transactional
    void deleteBySessionId(UUID sessionId);
}
