package com.vuk.spring_webapp.exception;

public class JobPostingClosedException extends RuntimeException {
    public JobPostingClosedException(String message) {
        super(message);
    }
}
