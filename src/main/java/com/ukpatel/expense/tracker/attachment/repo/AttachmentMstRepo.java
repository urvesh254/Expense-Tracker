package com.ukpatel.expense.tracker.attachment.repo;

import com.ukpatel.expense.tracker.attachment.entity.AttachmentMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentMstRepo extends JpaRepository<AttachmentMst, Long> {

}
