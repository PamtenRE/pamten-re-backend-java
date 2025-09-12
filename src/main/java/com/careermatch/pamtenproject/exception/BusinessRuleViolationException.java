package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when business rules are violated
 * Maps to HTTP 400 Bad Request
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}