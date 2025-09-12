package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when an invalid role is assigned or requested
 * Maps to HTTP 400 Bad Request
 */
public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String message) {
        super(message);
    }

    public InvalidRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}