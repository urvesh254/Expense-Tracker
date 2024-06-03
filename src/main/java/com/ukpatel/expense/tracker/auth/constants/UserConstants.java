package com.ukpatel.expense.tracker.auth.constants;

public final class UserConstants {

    public static final Long PWD_CHANGE_TYPE_NEW_USER = 1L;
    public static final Long PWD_CHANGE_TYPE_CHANGE_PASSWORD = 2L;
    public static final Long PWD_CHANGE_TYPE_FORGOT_PASSWORD = 3L;

    // Prevent instantiation
    private UserConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

}
