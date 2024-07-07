package com.ukpatel.expense.tracker.mail.service.impl.mailerSend.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("mail.server.mailer-send")
public class MailerSendProperty {

    private String fromName;
    private String fromEmailId;
    private String apiToken;
    private String authenticationCodeMailTemplateId;
    private String forgotPasswordEmailSubject;
}
