package com.ukpatel.expense.tracker.mail.service;

import java.util.Map;

public interface MailSender {

    Map<String, Object> sendMail(Map<String, Object> args);
}
