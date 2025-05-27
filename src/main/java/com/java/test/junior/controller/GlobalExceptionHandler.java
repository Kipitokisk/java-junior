package com.java.test.junior.controller;

import com.java.test.junior.exception.ForbiddenException;
import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.model.Response;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.java.test.junior.util.ResponseUtil.getErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse("Validation failed: " + errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation: {}", ex.getMessage());

        String userMessage = "Data conflict";

        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof PSQLException) {
            String fullMessage = rootCause.getMessage();
            userMessage = extractPostgresMessage(fullMessage);
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(getErrorResponse("Error creating product: " + userMessage));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Response> handleSQLException(SQLException ex) {
        logger.warn("Database error occurred: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getErrorResponse("Database error: " + extractPostgresMessage(ex.getMessage())));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("Constraint violation: {}", ex.getMessage());

        String violations = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(getErrorResponse("Validation failed: " + violations));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response> handleForbiddenException(ForbiddenException ex) {
        logger.warn("Forbidden access: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Response> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        logger.warn("Resource already exists: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorResponse(ex.getMessage()));
    }

    private String extractPostgresMessage(String message) {
        return message.split("\n")[1];
    }

}

