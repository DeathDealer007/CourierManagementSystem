package com.capg.smartcourier.exception;

/**
 * Resource Not Found Exception
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with error message
     *
     * Creates a new ResourceNotFoundException with a descriptive message
     * explaining what resource was not found.
     *
     * @param message descriptive message about the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}