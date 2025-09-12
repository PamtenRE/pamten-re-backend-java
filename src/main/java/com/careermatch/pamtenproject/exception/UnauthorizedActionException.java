package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when user attempts an unauthorized action
 * Maps to HTTP 403 Forbidden
 */
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }

    public UnauthorizedActionException(String message, Throwable cause) {
        super(message, cause);
    }
}