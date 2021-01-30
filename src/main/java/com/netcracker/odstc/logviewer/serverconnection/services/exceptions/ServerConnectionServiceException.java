package com.netcracker.odstc.logviewer.serverconnection.services.exceptions;

public class ServerConnectionServiceException extends Exception {

    public ServerConnectionServiceException(String message) {
        super(message);
    }

    public ServerConnectionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerConnectionServiceException(Throwable cause) {
        super(cause);
    }
}
