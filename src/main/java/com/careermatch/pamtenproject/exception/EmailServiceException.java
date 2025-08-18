package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when email service operations fail
 * Maps to HTTP 503 Service Unavailable
 */
public class EmailServiceException extends RuntimeException {

    public EmailServiceException(String message) {
        super(message);
    }

    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}