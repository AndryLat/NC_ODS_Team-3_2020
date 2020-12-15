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
    ERROR(16),
    FATAL(17);

    private int value;

    LogLevel(int valueArg) {
        value = valueArg;
    }

    public static LogLevel getByID(int id) {
        for (LogLevel l : values()) {
            if (l.value == id) {
                return l;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public int getValue() {
        return value;
    }

    public static boolean contains(String value) {
        for (LogLevel level : LogLevel.values()) {
            if (level.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
