package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when a requested resume is not found
 * Maps to HTTP 404 Not Found
 */
public class ResumeNotFoundException extends RuntimeException {

    public ResumeNotFoundException(String message) {
        super(message);
    }

    public ResumeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}