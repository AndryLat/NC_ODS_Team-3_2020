package com.netcracker.odstc.logviewer.service.exceptions;

public class DirectoryServiceException extends RuntimeException {
    public DirectoryServiceException(String message) {
        super(message);
    }

    public DirectoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryServiceException(Throwable cause) {
        super(cause);
    }

    public DirectoryServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
