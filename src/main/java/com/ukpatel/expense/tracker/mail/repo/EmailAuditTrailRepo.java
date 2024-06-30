package com.ukpatel.expense.tracker.mail.repo;

import com.ukpatel.expense.tracker.mail.entity.EmailAuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuditTrailRepo extends JpaRepository<EmailAuditTrail, Long> {

}
