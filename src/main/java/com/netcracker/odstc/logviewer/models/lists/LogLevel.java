package com.netcracker.odstc.logviewer.models.lists;

public enum LogLevel {
    SEVERE(9),
    WARNING(10),
    INFO(11),
    CONFIG(12),
    FINE(13),
    FINER(14),
    FINEST(15),
    DEBUG(16),
    TRACE(17),
    ERROR(18),
    FATAL(19);

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
