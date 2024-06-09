package com.team4.leaveprocessingsystem.exception;

public class PublicHolidayApiException extends RuntimeException {
    public PublicHolidayApiException(String message) {
        super(message);
    }

    public PublicHolidayApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
