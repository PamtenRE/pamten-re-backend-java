package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when attempting to authenticate with a deactivated account
 * Maps to HTTP 401 Unauthorized
 */
public class AccountDeactivatedException extends RuntimeException {

    public AccountDeactivatedException(String message) {
        super(message);
    }

    public AccountDeactivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}