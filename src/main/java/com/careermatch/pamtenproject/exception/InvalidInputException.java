package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when input validation fails
 * Maps to HTTP 422 Unprocessable Entity
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}