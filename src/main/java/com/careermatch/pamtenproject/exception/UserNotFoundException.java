package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when a requested user is not found
 * Maps to HTTP 404 Not Found
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}