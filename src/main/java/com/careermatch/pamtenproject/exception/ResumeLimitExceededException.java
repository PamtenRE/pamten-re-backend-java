package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when user exceeds the maximum number of resumes allowed
 * Maps to HTTP 400 Bad Request
 */
public class ResumeLimitExceededException extends RuntimeException {

    public ResumeLimitExceededException(String message) {
        super(message);
    }

    public ResumeLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}