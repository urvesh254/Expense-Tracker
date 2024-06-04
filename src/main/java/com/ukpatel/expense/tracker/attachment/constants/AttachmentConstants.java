package com.ukpatel.expense.tracker.attachment.constants;

public final class AttachmentConstants {

    public static final Short CREATED = 0;
    public static final Short SAVED = 1;
    public static final String OPTION_DOWNLOAD = "download";
    public static final String OPTION_VIEW = "view";



    // Prevent instantiation
    private AttachmentConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

}
