package com.team4.leaveprocessingsystem.exception;

public class CompensationClaimInvalidException extends RuntimeException {
    public CompensationClaimInvalidException(String message) {
        super("Bad Compensation Claim Input: " + message);
    }

    public CompensationClaimInvalidException(String message, Throwable cause) {
        super("Bad Compensation Claim Input: " + message, cause);
    }
}