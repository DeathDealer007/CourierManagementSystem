package com.capg.smartcourier.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid arg");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid arg", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("delivery", "trackingNumber", "required");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("errors"));
    }

    @Test
    void testHandleConstraintViolationException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("deliveryId");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("invalid");
        when(ex.getConstraintViolations()).thenReturn(Collections.singleton(violation));

        ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleGlobalException() {
        Exception ex = new Exception("Generic error");
        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Generic error"));
    }
}
