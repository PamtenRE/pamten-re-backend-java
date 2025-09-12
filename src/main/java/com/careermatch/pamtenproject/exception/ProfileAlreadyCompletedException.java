package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when attempting to complete an already completed profile
 * Maps to HTTP 400 Bad Request
 */
public class ProfileAlreadyCompletedException extends RuntimeException {

    public ProfileAlreadyCompletedException(String message) {
        super(message);
    }

    public ProfileAlreadyCompletedException(String message, Throwable cause) {
        super(message, cause);
    }
}