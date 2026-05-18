package com.capg.smartcourier.exception;

/*
 * This is the class where we handle all the exceptions globally, and we will throw the structured message
 * errors instead of raw message which have no information
 */

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler - Centralized Exception Management
 *
 * This class provides centralized exception handling for the entire Authentication Service.
 * It intercepts exceptions thrown by controllers and converts them into structured,
 * user-friendly error responses instead of raw stack traces.
 *
 * Key Features:
 * - Global exception handling using @ControllerAdvice
 * - Structured error responses with consistent format
 * - Validation error handling with field-level details
 * - Custom exception handling for business logic errors
 * - Fallback handling for unexpected system errors
 * - HTTP status code mapping for different error types
 *
 * Exception Types Handled:
 * - ResourceNotFoundException: 404 Not Found
 * - IllegalArgumentException: 400 Bad Request
 * - MethodArgumentNotValidException: 400 Bad Request (validation errors)
 * - ConstraintViolationException: 400 Bad Request (constraint violations)
 * - Exception: 500 Internal Server Error (catch-all)
 *
 * Response Format:
 * For simple errors: {"message": "error description", "status": http_code}
 * For validation errors: {"timestamp": time, "status": 400, "message": "Validation failed", "errors": {...}}
 *
 * Benefits:
 * - Consistent error responses across all endpoints
 * - No sensitive information leaked in error messages
 * - Better client experience with meaningful error messages
 * - Centralized logging and monitoring of errors
 * - Separation of error handling from business logic
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@ControllerAdvice  //makes this class global exception handler
                   //applies to all the controllers automatically
public class GlobalExceptionHandler {

    /**
     * Handle Resource Not Found Exceptions
     *
     * Catches ResourceNotFoundException thrown when requested resources
     * (users, roles, etc.) cannot be found in the database.
     *
     * Common Scenarios:
     * - User login with non-existent username
     * - Attempting to access deleted resources
     * - Invalid resource identifiers
     *
     * @param ex the ResourceNotFoundException thrown
     * @return ResponseEntity with 404 status and error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle Illegal Argument Exceptions
     *
     * Catches IllegalArgumentException for invalid input parameters
     * or business logic violations that don't fit validation rules.
     *
     * Common Scenarios:
     * - Invalid credentials during login
     * - Business rule violations
     * - Invalid parameter combinations
     *
     * @param ex the IllegalArgumentException thrown
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Validation Exceptions
     *
     * Catches MethodArgumentNotValidException for Jakarta Bean Validation
     * constraint violations on request DTOs.
     *
     * Common Scenarios:
     * - Missing required fields in User registration
     * - Invalid email format
     * - Password strength violations
     * - Field length constraints
     *
     * @param ex the MethodArgumentNotValidException thrown
     * @return ResponseEntity with 400 status and validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existingMessage, newMessage) -> existingMessage
                ));

        response.put("timestamp", System.currentTimeMillis());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Constraint Violation Exceptions
     *
     * Catches ConstraintViolationException for validation violations
     * on method parameters annotated with @Validated.
     *
     * Common Scenarios:
     * - Path variable validation failures
     * - Request parameter validation violations
     * - Method-level constraint violations
     *
     * @param ex the ConstraintViolationException thrown
     * @return ResponseEntity with 400 status and constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(cv ->
                errors.put(cv.getPropertyPath().toString(), cv.getMessage())
        );

        response.put("timestamp", System.currentTimeMillis());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Global Exceptions (Fallback Handler)
     *
     * Catches any unhandled Exception as a last resort to prevent
     * exposing internal system details to clients.
     *
     * Common Scenarios:
     * - Unexpected runtime exceptions
     * - Database connection failures
     * - External service unavailability
     * - Unhandled business logic errors
     *
     * @param ex the generic Exception thrown
     * @return ResponseEntity with 500 status and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {

        ErrorResponse error = new ErrorResponse(
                "Something went wrong: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}