package com.capg.smartcourier.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Custom exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND
        );
    }

    // 🔥 Feign Exception (IMPORTANT)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {

        String message = "Downstream service error";

        if (ex.status() == 404) {
            message = "Requested resource not found in another service";
        } else if (ex.status() == 500) {
            message = "Internal error in another service";
        }

        return new ResponseEntity<>(
                new ErrorResponse(message, ex.status()),
                HttpStatus.valueOf(ex.status())
        );
    }

    // 🔹 Handle validation errors from @Valid annotation
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

    // 🔹 Handle constraint violations from @Validated
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

    // 🔹 General exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex) {

        return new ResponseEntity<>(
                new ErrorResponse("Something went wrong: " + ex.getMessage(), 500),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}