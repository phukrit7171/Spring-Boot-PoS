package com.pos.phukrit.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        String requestDetails = request.getDescription(false);
        logger.error("Resource not found: {} - Request: {}", ex.getMessage(), requestDetails);
        ErrorDetails errorDetails = new ErrorDetails(Instant.now(), ex.getMessage(), requestDetails);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String requestDetails = request.getDescription(false);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        
        String errorMessage = errors.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(", "));
            
        logger.error("Validation error: {} - Request: {}", errorMessage, requestDetails);
        ErrorDetails errorDetails = new ErrorDetails(Instant.now(), "Validation failed: " + errorMessage, requestDetails);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        String requestDetails = request.getDescription(false);
        logger.error("Unexpected error: {} - Request: {}", ex.getMessage(), requestDetails, ex);
        ErrorDetails errorDetails = new ErrorDetails(Instant.now(), ex.getMessage(), requestDetails);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}