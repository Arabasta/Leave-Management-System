package com.team4.leaveprocessingsystem.exception;

public class CompensationClaimNotFoundException extends RuntimeException {
    public CompensationClaimNotFoundException(String message) {
        super("Compensation Claim not found: " + message);
    }

    public CompensationClaimNotFoundException(String message, Throwable cause) {
        super("Compensation Claim not found: " + message, cause);
    }
}