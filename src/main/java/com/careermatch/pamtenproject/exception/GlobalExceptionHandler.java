package com.careermatch.pamtenproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ===== VALIDATION EXCEPTIONS (400 Bad Request) =====

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation error: {}", errors);
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", "Validation failed", errors);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("File upload size exceeded: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("FILE_TOO_LARGE", "File size exceeds maximum allowed limit");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    // ===== BUSINESS RULE VIOLATIONS (400 Bad Request) =====

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            InvalidRoleException.class,
            ProfileAlreadyCompletedException.class,
            ResumeLimitExceededException.class,
            BusinessRuleViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(RuntimeException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("BUSINESS_RULE_VIOLATION", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    // ===== AUTHENTICATION EXCEPTIONS (401 Unauthorized) =====

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("AUTHENTICATION_ERROR", "Authentication failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            AccountDeactivatedException.class,
            TokenExpiredException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomAuthenticationExceptions(RuntimeException ex) {
        log.warn("Authentication issue: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("AUTHENTICATION_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ===== AUTHORIZATION EXCEPTIONS (403 Forbidden) =====

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("ACCESS_DENIED", "You don't have permission to perform this action");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({
            UnauthorizedActionException.class,
            InsufficientPrivilegesException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomAuthorizationExceptions(RuntimeException ex) {
        log.warn("Authorization issue: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("AUTHORIZATION_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ===== RESOURCE NOT FOUND EXCEPTIONS (404 Not Found) =====

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        ErrorResponse error = new ErrorResponse("NOT_FOUND", "The requested resource was not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            ResourceNotFoundException.class,
            JobNotFoundException.class,
            ResumeNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleResourceNotFoundExceptions(RuntimeException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ===== VALIDATION EXCEPTIONS (422 Unprocessable Entity) =====

    @ExceptionHandler({
            InvalidInputException.class,
            FileValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomValidationExceptions(RuntimeException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    // ===== SERVICE UNAVAILABLE EXCEPTIONS (503 Service Unavailable) =====

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(ServiceUnavailableException ex) {
        log.error("Service unavailable: ", ex);
        ErrorResponse error = new ErrorResponse("SERVICE_UNAVAILABLE", "Service temporarily unavailable. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler({
            EmailServiceException.class,
            FileStorageException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomServiceExceptions(RuntimeException ex) {
        log.error("Service error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("SERVICE_ERROR", "Service temporarily unavailable. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    // ===== DATABASE AND SYSTEM EXCEPTIONS =====

    @ExceptionHandler({DataAccessException.class, SQLException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseException(Exception ex) {
        log.error("Database error occurred: ", ex);
        ErrorResponse error = new ErrorResponse("DATABASE_ERROR", "Database operation failed. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    // In GlobalExceptionHandler.java, add this handler:
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("File I/O error occurred: ", ex);
        ErrorResponse error = new ErrorResponse("FILE_ERROR", "File operation failed. Please try again.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ===== FALLBACK EXCEPTION HANDLERS =====

    // Handle any other RuntimeException (fallback for business logic)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: ", ex);
        ErrorResponse error = new ErrorResponse("BUSINESS_ERROR", "An unexpected business error occurred. Please try again.");
        return ResponseEntity.badRequest().body(error);
    }

    // Handle any other unexpected exceptions (ultimate fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ===== ERROR RESPONSE CLASS =====

    public static class ErrorResponse {
        private String error;
        private String message;
        private Map<String, String> details;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public ErrorResponse(String error, String message, Map<String, String> details) {
            this.error = error;
            this.message = message;  // Fixed: was incorrectly assigning 'details' to 'message'
            this.details = details;
        }

        // Getters and setters
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, String> getDetails() { return details; }
        public void setDetails(Map<String, String> details) { this.details = details; }
    }
}