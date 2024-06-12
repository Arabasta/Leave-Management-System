package com.team4.leaveprocessingsystem.exception;

public class LeaveApplicationNotFoundException extends RuntimeException {
    public LeaveApplicationNotFoundException(String message) {
        super(message);
    }

    public LeaveApplicationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}