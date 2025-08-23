package com.pos.phukrit.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        
        // Extract field errors
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    fieldError -> fieldError.getField(),
                    fieldError -> fieldError.getDefaultMessage()
                ));
        
        response.put("message", "Validation failed");
        response.put("errors", errors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Conflict");
        
        // Parse the constraint violation message to provide better user feedback
        String message = ex.getMessage();
        
        // Log the full exception message for debugging
        System.err.println("DataIntegrityViolationException: " + message);
        
        if (message != null) {
            if (message.contains("UKR43AF9AP4EDM43MMTQ01ODDJ6") || message.contains("username")) {
                response.put("message", "Username already exists. Please choose a different username.");
                response.put("field", "username");
                
                // Extract the conflicting value for debugging
                if (message.contains("VALUES ( /* 1 */")) {
                    int start = message.indexOf("VALUES ( /* 1 */ '") + "VALUES ( /* 1 */ '".length();
                    int end = message.indexOf("'", start);
                    if (start < end && end > 0) {
                        String conflictingValue = message.substring(start, end);
                        response.put("conflicting_value", conflictingValue);
                        response.put("debug_info", "Attempted username: '" + conflictingValue + "'");
                    }
                }
            } else if (message.contains("UK6DOTKOTT2KJSP8VW4D0M25FB7") || message.contains("email")) {
                response.put("message", "Email address already exists. Please use a different email.");
                response.put("field", "email");
                
                // Extract the conflicting value for debugging
                if (message.contains("VALUES ( /* 1 */")) {
                    int start = message.indexOf("VALUES ( /* 1 */ '") + "VALUES ( /* 1 */ '".length();
                    int end = message.indexOf("'", start);
                    if (start < end && end > 0) {
                        String conflictingValue = message.substring(start, end);
                        response.put("conflicting_value", conflictingValue);
                        response.put("debug_info", "Attempted email: '" + conflictingValue + "'");
                    }
                }
            } else if (message.contains("phone_number")) {
                response.put("message", "Phone number already exists. Please use a different phone number.");
                response.put("field", "phone_number");
            } else {
                response.put("message", "A record with this information already exists. Please check your input.");
                response.put("debug_constraint_error", message);
            }
        } else {
            response.put("message", "Data already exists. Please check your input.");
        }
        
        response.put("path", "/api/users");
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Unauthorized");
        response.put("message", "Invalid username or password.");
        response.put("path", "/api/auth/login");
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Forbidden");
        response.put("message", "You don't have permission to access this resource.");
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred. Please try again later.");
        
        // Log the actual exception for debugging purposes
        System.err.println("Unexpected RuntimeException: " + ex.getMessage());
        ex.printStackTrace();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
