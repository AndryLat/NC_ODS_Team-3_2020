package com.netcracker.odstc.logviewer.serverconnection.exceptions;

public class ServerLogProcessingException extends RuntimeException {
    public ServerLogProcessingException(String message) {
        super(message);
    }

    public ServerLogProcessingException(Exception exception) {
        super(exception.getMessage(), exception.getCause());
    }
}
