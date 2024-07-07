package com.ukpatel.expense.tracker.attachment.exception;

import com.ukpatel.expense.tracker.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class AttachmentException extends ApplicationException {

    public AttachmentException(String message) {
        super(message);
    }

    public AttachmentException(HttpStatus status, String message) {
        super(status, message);
    }
}
