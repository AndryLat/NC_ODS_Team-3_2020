package com.netcracker.odstc.logviewer.containers;

public enum SortType {
    BY_DATE(0),
    BY_LEVEL_AND_DATE(1);

    private final int code;
    SortType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
