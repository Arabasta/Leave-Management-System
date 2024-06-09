package com.team4.leaveprocessingsystem.exception;

public class ServiceSaveException extends RuntimeException {
    public ServiceSaveException(String message) {
        super("Failed to save entity: " + message);
    }

    public ServiceSaveException(String message, Throwable cause) {
        super("Failed to save entity: " + message, cause);
    }

}
