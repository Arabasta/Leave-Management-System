package com.team4.leaveprocessingsystem.exception;

public class CompensationClaimNotFoundException extends RuntimeException {
    public CompensationClaimNotFoundException(String message) {
        super("Bad Compensation Claim Input: " + message);
    }

    public CompensationClaimNotFoundException(String message, Throwable cause) {
        super("Bad Compensation Claim Input: " + message, cause);
    }
}