package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when file validation fails
 * Maps to HTTP 422 Unprocessable Entity
 */
public class FileValidationException extends RuntimeException {

    public FileValidationException(String message) {
        super(message);
    }

    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}