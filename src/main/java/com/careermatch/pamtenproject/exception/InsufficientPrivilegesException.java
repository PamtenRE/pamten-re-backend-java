package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when user lacks sufficient privileges for an action
 * Maps to HTTP 403 Forbidden
 */
public class InsufficientPrivilegesException extends RuntimeException {

    public InsufficientPrivilegesException(String message) {
        super(message);
    }

    public InsufficientPrivilegesException(String message, Throwable cause) {
        super(message, cause);
    }
}