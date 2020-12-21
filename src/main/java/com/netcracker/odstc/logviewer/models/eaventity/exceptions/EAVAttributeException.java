package com.netcracker.odstc.logviewer.models.eaventity.exceptions;

public class EAVAttributeException extends RuntimeException {

    public static final String NON_EXISTING_ATTRIBUTE = "Accessing non existing attribute";
    public static final String NON_EXISTING_REFERENCE = "Accessing non existing reference";

    public EAVAttributeException(String message) {
        super(message);
    }
}
