package com.netcracker.odstc.logviewer.models.lists;

public enum LogLevel {
    SEVERE(13),
    WARNING(14),
    INFO(15),
    CONFIG(16),
    FINE(17),
    FINER(18),
    FINEST(19),
    DEBUG(20),
    TRACE(21),
    ERROR(22),
    FATAL(23),
    NO_LEVEL(24);

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

    public static boolean contains(String value) {
        for (LogLevel level : LogLevel.values()) {
            if (level.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public int getValue() {
        return value;
    }
}
