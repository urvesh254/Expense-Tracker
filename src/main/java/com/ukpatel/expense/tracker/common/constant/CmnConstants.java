package com.ukpatel.expense.tracker.common.constant;

public final class CmnConstants {

    public static final Short STATUS_ACTIVE = 1;
    public static final Short STATUS_INACTIVE = 0;

    // Prevent instantiation
    private CmnConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
