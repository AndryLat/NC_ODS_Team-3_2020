package com.netcracker.odstc.logviewer.service.exceptions;

public class LogFileServiceException extends RuntimeException {
    public LogFileServiceException(String message) {
        super(message);
    }

    public LogFileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogFileServiceException(Throwable cause) {
        super(cause);
    }

    public LogFileServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
