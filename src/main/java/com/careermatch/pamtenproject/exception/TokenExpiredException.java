package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when JWT token has expired
 * Maps to HTTP 401 Unauthorized
 */
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}