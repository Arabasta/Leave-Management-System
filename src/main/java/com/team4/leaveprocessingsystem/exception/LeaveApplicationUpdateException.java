package com.team4.leaveprocessingsystem.exception;

public class LeaveApplicationUpdateException extends RuntimeException{

    public LeaveApplicationUpdateException(String message) {
        super(message);
    }

    public LeaveApplicationUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

