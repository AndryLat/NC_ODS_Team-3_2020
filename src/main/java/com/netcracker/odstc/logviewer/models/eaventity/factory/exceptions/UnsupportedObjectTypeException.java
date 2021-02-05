package com.netcracker.odstc.logviewer.models.eaventity.factory.exceptions;

public class UnsupportedObjectTypeException extends RuntimeException {
    public UnsupportedObjectTypeException() {
        super();
    }

    public UnsupportedObjectTypeException(String message) {
        super(message);
    }

    public UnsupportedObjectTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedObjectTypeException(Throwable cause) {
        super(cause);
    }
}
