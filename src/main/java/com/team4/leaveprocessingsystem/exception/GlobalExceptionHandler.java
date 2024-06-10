package com.team4.leaveprocessingsystem.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// listens for all exceptions thrown from all controllers
@ControllerAdvice
public class GlobalExceptionHandler {
    // add custom exceptions here

    @ExceptionHandler(CompensationClaimInvalidException.class)
    public ResponseEntity<Object> handleCompensationClaimInvalidException(CompensationClaimInvalidException e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(ServiceSaveException.class)
    public ResponseEntity<Object> handleServiceSaveException(ServiceSaveException e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }

}