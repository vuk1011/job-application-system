package com.vuk.spring_webapp.exception;

public class ApplicationExistsException extends RuntimeException {
    public ApplicationExistsException(String message) {
        super(message);
    }
}
