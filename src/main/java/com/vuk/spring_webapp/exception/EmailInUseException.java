package com.vuk.spring_webapp.exception;

public class EmailInUseException extends RuntimeException {
    public EmailInUseException(String message) {
        super(message);
    }
}
