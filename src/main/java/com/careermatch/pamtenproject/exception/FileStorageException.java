package com.careermatch.pamtenproject.exception;

/**
 * Exception thrown when file storage operations fail
 * Maps to HTTP 503 Service Unavailable
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
