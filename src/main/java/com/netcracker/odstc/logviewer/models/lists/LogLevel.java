package com.netcracker.odstc.logviewer.models.lists;

public enum LogLevel {
    SEVERE(7),
    WARNING(8),
    INFO(9),
    CONFIG(10),
    FINE(11),
    FINER(12),
    FINEST(13),
    DEBUG(14),
    TRACE(15),
    ERROR(17),
    FATAL(18);

    private int value;

    LogLevel(int valueArg) {
        value = valueArg;
    }

    public int getValue() {
        return value;
    }
}
