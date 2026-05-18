package com.capg.smartcourier.exception;

import java.time.LocalDateTime;

/**
 * Error Response DTO
 *
 * Data transfer object for standardized error responses in the Smart Courier System.
 * Provides consistent error information format across all microservices APIs.
 * Used by GlobalExceptionHandler to return structured error information to clients.
 */
public class ErrorResponse {

    /**
     * Timestamp when the error occurred
     * Automatically set to current time in constructor
     * Helps with debugging and error tracking
     */
    private LocalDateTime timestamp;

    /**
     * Human-readable error message
     * Describes what went wrong in user-friendly terms
     * Should be appropriate for client-side display
     */
    private String message;

    /**
     * HTTP status code as integer
     * Corresponds to standard HTTP status codes
     * Used by clients to determine error type
     */
    private int status;

    /**
     * Default constructor
     * Required for JSON deserialization and framework instantiation
     */
    public ErrorResponse() {}

    /**
     * Parameterized constructor
     *
     * Creates an ErrorResponse with message and status.
     * Automatically sets timestamp to current time.
     *
     * @param message the error message describing what went wrong
     * @param status the HTTP status code as integer
     */
    public ErrorResponse(String message, int status) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.status = status;
    }

    // Getters & Setters

    /**
     * Get the error timestamp
     * @return the timestamp when the error occurred
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Set the error timestamp
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the error message
     * @return the human-readable error description
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the error message
     * @param message the error message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the HTTP status code
     * @return the status code as integer
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the HTTP status code
     * @param status the status code to set
     */
    public void setStatus(int status) {
        this.status = status;
    }
}