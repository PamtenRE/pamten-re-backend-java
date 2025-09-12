package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when a requested job is not found
 * Maps to HTTP 404 Not Found
 */
public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}