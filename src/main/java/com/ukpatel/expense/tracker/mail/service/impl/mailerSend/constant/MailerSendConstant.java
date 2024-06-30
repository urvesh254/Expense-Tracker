package com.ukpatel.expense.tracker.mail.service.impl.mailerSend.constant;

public final class MailerSendConstant {

    public static final String KEY_API_KEY = "KEY_API_KEY";
    public static final String KEY_FROM_NAME = "KEY_FROM_NAME";
    public static final String KEY_FROM_EMAIL_ID = "KEY_FROM_EMAIL_ID";
    public static final String KEY_TO_NAME = "KEY_TO_NAME";
    public static final String KEY_TO_EMAIL_ID = "KEY_TO_EMAIL_ID";
    public static final String KEY_EMAIL_SUBJECT = "KEY_EMAIL_SUBJECT";
    public static final String KEY_IS_TEMPLATE = "KEY_IS_TEMPLATE";
    public static final String KEY_EMAIL_TEMPLATE_ID = "KEY_EMAIL_TEMPLATE_ID";
    public static final String KEY_EMAIL_TEMPLATE_PARAMETERS = "KEY_EMAIL_TEMPLATE_PARAMETERS";
    public static final String KEY_EMAIL_PLAIN_BODY = "KEY_EMAIL_PLAIN_BODY";
    public static final String KEY_EMAIL_HTML_BODY = "KEY_EMAIL_HTML_BODY";


    // Prevent instantiation
    private MailerSendConstant() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
