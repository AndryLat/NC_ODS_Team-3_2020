package com.netcracker.odstc.logviewer.serverconnection.exceptions;

public class ServerConnectionException extends RuntimeException {
    public ServerConnectionException(String message) {
        super(message);
    }

    public ServerConnectionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
