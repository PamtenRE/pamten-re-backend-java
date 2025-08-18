package com.careermatch.pamtenproject.exception;

/**
 * Generic exception thrown when a requested resource is not found
 * Maps to HTTP 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}