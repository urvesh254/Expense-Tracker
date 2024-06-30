package com.ukpatel.expense.tracker.auth.constants;

import java.util.List;

public final class UserConstants {

    public static final Long PWD_CHANGE_TYPE_NEW_USER = 1L;
    public static final Long PWD_CHANGE_TYPE_CHANGE_PASSWORD = 2L;
    public static final Long PWD_CHANGE_TYPE_FORGOT_PASSWORD = 3L;

    public static final Integer AUTH_CODE_LENGTH = 6;
    public static final Integer VERIFICATION_CODE_LENGTH = 18;
    public static final String KEY_VERIFICATION_CODE = "verificationCode";
    public static final String KEY_VALID_TILL = "validTill";
    public static final String KEY_EMAIL = "email";
    public static final List<String> ALLOWED_EXTENSIONS_FOR_PROFILE = List.of(".jpg", ".jpeg", ".png");

    // Prevent instantiation
    private UserConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

}
