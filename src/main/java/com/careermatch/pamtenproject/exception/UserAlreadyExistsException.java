package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when attempting to create a user that already exists
 * Maps to HTTP 400 Bad Request
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}