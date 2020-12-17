package com.netcracker.odstc.logviewer.models.exceptions;

public class IllegalDirectoryStateException extends RuntimeException {
    public static final String UNKNOWN_DIRECTORY_STATE = "Getting unknown directory state";

    public IllegalDirectoryStateException(String message){
        super(message);
    }
}
