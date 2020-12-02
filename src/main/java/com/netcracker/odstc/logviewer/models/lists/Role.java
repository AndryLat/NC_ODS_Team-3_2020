package com.netcracker.odstc.logviewer.models.lists;

public enum Role {
    ADMIN(1),
    USER(2);

    private int value;

    Role(int valueArg) {
        value = valueArg;
    }

    public int getValue() {
        return value;
    }
}
