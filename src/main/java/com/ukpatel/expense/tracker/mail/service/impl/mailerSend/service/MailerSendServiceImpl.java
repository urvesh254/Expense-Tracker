package com.ukpatel.expense.tracker.mail.service.impl.mailerSend.service;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.ukpatel.expense.tracker.mail.service.MailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.ukpatel.expense.tracker.mail.constant.MailConstants.*;
import static com.ukpatel.expense.tracker.mail.service.impl.mailerSend.constant.MailerSendConstant.*;

@Service
public class MailerSendServiceImpl implements MailSender {

    @Override
    public Map<String, Object> sendMail(Map<String, Object> args) {

        // Retrieving Parameters
        final Object defaultValue = "";
        String apiToken = args.getOrDefault(KEY_API_KEY, defaultValue).toString();
        String fromName = args.getOrDefault(KEY_FROM_NAME, defaultValue).toString();
        String fromEmailId = args.getOrDefault(KEY_FROM_EMAIL_ID, defaultValue).toString();
        String toName = args.getOrDefault(KEY_TO_NAME, defaultValue).toString();
        String toEmailId = args.getOrDefault(KEY_TO_EMAIL_ID, defaultValue).toString();
        String subject = args.getOrDefault(KEY_EMAIL_SUBJECT, defaultValue).toString();
        String plainBody = args.getOrDefault(KEY_EMAIL_PLAIN_BODY, defaultValue).toString();
        String htmlBody = args.getOrDefault(KEY_EMAIL_HTML_BODY, defaultValue).toString();
        boolean isTemplate = Boolean.parseBoolean(args.getOrDefault(KEY_IS_TEMPLATE, "false").toString());
        String templateId = args.getOrDefault(KEY_EMAIL_TEMPLATE_ID, defaultValue).toString();
        Map<String, String> templateParameters = (Map<String, String>) args.getOrDefault(KEY_EMAIL_TEMPLATE_PARAMETERS, new HashMap<>());

        Email email = new Email();

        email.setFrom(fromName, fromEmailId);
        email.addRecipient(toName, toEmailId);
        email.setSubject(subject);

        if (isTemplate) {
            email.setTemplateId(templateId);
            templateParameters.forEach(email::addPersonalization);
        }

        if (!plainBody.isBlank()) email.setPlain(plainBody);
        if (!htmlBody.isBlank()) email.setPlain(htmlBody);

        MailerSend ms = new MailerSend();
        ms.setToken(apiToken);

        Map<String, Object> res = new HashMap<>();
        Map<String, Object> resBody = new HashMap<>();
        try {
            MailerSendResponse response = ms.emails().send(email);
            resBody.put("messageId", response.messageId);

            res.put(KEY_RES_STATUS_CODE, response.responseStatusCode);
            res.put(KEY_RES_BODY, resBody);
        } catch (MailerSendException e) {
            res.put(KEY_RES_ERROR, e.getMessage());
        }
        return res;
    }
}
