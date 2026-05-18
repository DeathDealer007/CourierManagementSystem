package com.capg.smartcourier.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Global Exception Handler
 *
 * Centralized exception handling for the Delivery Service microservice.
 * Provides consistent error responses across all REST endpoints.
 * Converts various exception types to appropriate HTTP status codes and messages.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle Resource Not Found Exceptions
     *
     * Catches ResourceNotFoundException thrown when requested deliveries,
     * parcels, or addresses cannot be found in the database.
     *
     * Common Scenarios:
     * - Delivery not found by ID
     * - User deliveries not found
     * - Invalid delivery references
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
     * - Invalid delivery status transitions
     * - Invalid service type values
     * - Business rule violations
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
     * constraint violations on request DTOs and entity objects.
     *
     * Common Scenarios:
     * - Missing required fields in delivery creation
     * - Invalid tracking number format
     * - Invalid email or phone formats
     * - Numeric field constraints violations
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
     * - Database connection failures
     * - Message queue communication errors
     * - Unexpected runtime exceptions
     * - External service unavailability
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