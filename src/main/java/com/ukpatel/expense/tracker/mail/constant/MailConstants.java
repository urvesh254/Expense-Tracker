package com.ukpatel.expense.tracker.mail.constant;

public class MailConstants {

    public static final String KEY_RES_STATUS_CODE = "KEY_RES_STATUS_CODE";
    public static final String KEY_RES_BODY = "KEY_RES_BODY";
    public static final String KEY_RES_ERROR = "KEY_RES_ERROR";
    public static final String KEY_EMAIL_AUDIT_ID = "KEY_EMAIL_AUDIT_ID";

    // Prevent instantiation
    private MailConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
