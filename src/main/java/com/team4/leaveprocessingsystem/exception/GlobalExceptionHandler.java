package com.team4.leaveprocessingsystem.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// listens for all exceptions thrown from all controllers
@ControllerAdvice
public class GlobalExceptionHandler {
    // add custom exceptions here

    @ExceptionHandler(CompensationClaimNotFoundException.class)
    public ResponseEntity<Object> handleCompensationClaimNotFoundException(CompensationClaimNotFoundException e) {
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

    @ExceptionHandler(LeaveApplicationNotFoundException.class)
    public ResponseEntity<Object> handleLeaveApplicationNotFoundException(LeaveApplicationNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(LeaveApplicationUpdateException.class)
    public ResponseEntity<Object> handleLeaveApplicationUpdateException(LeaveApplicationUpdateException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }
}
