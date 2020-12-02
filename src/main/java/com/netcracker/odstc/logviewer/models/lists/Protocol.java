package com.netcracker.odstc.logviewer.models.lists;

public enum Protocol {
    SSH(3),
    FTP(4);

    private int value;

    Protocol(int valueArg) {
        value = valueArg;
    }

    public int getValue() {
        return value;
    }
}
