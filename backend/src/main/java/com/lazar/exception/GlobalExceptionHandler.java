package com.lazar.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApi(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of(
            "timestamp", Instant.now(), "status", ex.getStatus().value(), "error", ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .findFirst().map(e -> e.getField() + ": " + e.getDefaultMessage()).orElse("Invalid request");
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "status", 400, "error", message));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "status", 400, "error", "Malformed request or unsupported workflow"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("timestamp", Instant.now(), "status", 500, "error", "Unexpected server error"));
    }
}
