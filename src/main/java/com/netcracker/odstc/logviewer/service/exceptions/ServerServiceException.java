package com.netcracker.odstc.logviewer.service.exceptions;

public class ServerServiceException extends RuntimeException {

    public ServerServiceException(String message) {
        super(message);
    }

    public ServerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
