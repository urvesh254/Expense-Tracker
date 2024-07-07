package com.ukpatel.expense.tracker.attachment.repo;

import com.ukpatel.expense.tracker.attachment.entity.AttachmentMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AttachmentMstRepo extends JpaRepository<AttachmentMst, Long> {

    @Transactional
    @Modifying
    @Query("""
            DELETE
             FROM
             	AttachmentMst
             WHERE
             	attachmentId IN (
             		SELECT
             			am.attachmentId
             		FROM
             			AttachmentMst am
             		LEFT JOIN AttachmentFileMpg afm ON
             			am.attachmentId = afm.attachmentMst.attachmentId
             		WHERE
             			afm.attachmentMst.attachmentId IS NULL
             	)
            """)
    void deleteAllUnusedAttachments();
}
