package com.netcracker.odstc.logviewer.models.lists;

public enum Protocol {
    SSH(3),
    FTP(4);

    private int value;

    Protocol(int valueArg) {
        value = valueArg;
    }

    public static Protocol getByID(int id) {
        for (Protocol p : values()) {
            if (p.value == id) {
                return p;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public int getValue() {
        return value;
    }
}
