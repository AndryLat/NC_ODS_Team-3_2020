package com.netcracker.odstc.logviewer.models.exceptions;

public class IllegalServerStateException extends RuntimeException {
    public static final String UNKNOWN_SERVER_STATE = "Getting unknown server state";

    public IllegalServerStateException(String message) {
        super(message);
    }
}
