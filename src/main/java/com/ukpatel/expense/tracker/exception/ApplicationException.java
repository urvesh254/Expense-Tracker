package com.ukpatel.expense.tracker.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    public ApplicationException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ApplicationException {" + "status=" + status + ",message=" + getMessage() + '}';
    }
}
