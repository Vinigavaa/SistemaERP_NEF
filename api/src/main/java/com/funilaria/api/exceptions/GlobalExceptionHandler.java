package com.funilaria.api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateWorkException.class)
    public ResponseEntity<String> handleDuplicateWork(DuplicateWorkException ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }
}
