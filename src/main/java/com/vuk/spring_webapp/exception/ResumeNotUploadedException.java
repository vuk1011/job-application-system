package com.vuk.spring_webapp.exception;

public class ResumeNotUploadedException extends RuntimeException {
    public ResumeNotUploadedException(String message) {
        super(message);
    }
}
