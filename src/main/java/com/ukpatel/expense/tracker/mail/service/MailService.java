package com.ukpatel.expense.tracker.mail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukpatel.expense.tracker.mail.entity.EmailAuditTrail;
import com.ukpatel.expense.tracker.mail.repo.EmailAuditTrailRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    public final MailSender mailSender;
    public final EmailAuditTrailRepo emailAuditTrailRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Object> sendMail(Map<String, Object> args) throws Exception {
        Map<String, Object> response = mailSender.sendMail(args);

        ObjectMapper json = new ObjectMapper();
        EmailAuditTrail emailAuditTrail = new EmailAuditTrail();
        emailAuditTrail.setRequestData(json.writeValueAsString(args));
        emailAuditTrail.setResponseData(json.writeValueAsString(response));
        emailAuditTrailRepo.save(emailAuditTrail);
        return response;
    }
}
