package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when invalid credentials are provided during authentication
 * Maps to HTTP 401 Unauthorized
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}