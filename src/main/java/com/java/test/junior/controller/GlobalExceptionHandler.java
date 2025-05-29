package com.java.test.junior.controller;

import com.java.test.junior.exception.ForbiddenException;
import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.exception.UnauthorizedException;
import com.java.test.junior.model.Response;
import lombok.extern.java.Log;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.java.test.junior.util.ResponseUtil.getErrorResponse;

@RestControllerAdvice
@Log
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warning("Validation error: " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse("Validation failed: " + errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warning("Data integrity violation: " + ex.getMessage());

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
        log.warning("Database error occurred: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getErrorResponse("Database error: " + extractPostgresMessage(ex.getMessage())));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warning("Constraint violation: " + ex.getMessage());

        String violations = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(getErrorResponse("Validation failed: " + violations));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warning("Resource not found: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response> handleForbiddenException(ForbiddenException ex) {
        log.warning("Forbidden access: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Response> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        log.warning("Resource already exists: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Response> handleUnauthorizedException(UnauthorizedException ex) {
        log.warning("Unauthorized access: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Response> handleIOException(IOException ex) {
        log.warning("IO error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getErrorResponse("Failed to read the CSV file"));
    }

    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<Response> handleJdbcConnectionException(CannotGetJdbcConnectionException ex) {
        log.warning("Database connection failed: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(getErrorResponse("Database connection failed"));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Response> handleFileNotFoundException(FileNotFoundException ex) {
        log.warning("File not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(getErrorResponse("CSV file not found at the specified location"));
    }

    private String extractPostgresMessage(String message) {
        return message.split("\n")[1];
    }

}

