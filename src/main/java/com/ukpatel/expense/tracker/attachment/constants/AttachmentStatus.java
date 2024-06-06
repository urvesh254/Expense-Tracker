package com.ukpatel.expense.tracker.attachment.constants;

import lombok.Getter;

@Getter
public enum AttachmentStatus {
    CREATED(2), ACTIVE(1), INACTIVE(0);

    private final short status;

    AttachmentStatus(int status) {
        this.status = Short.parseShort(String.valueOf(status));
    }
}
