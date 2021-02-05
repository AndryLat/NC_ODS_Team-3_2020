package com.netcracker.odstc.logviewer.models.lists;

public enum Role {
    ADMIN(1),
    USER(2);

    private int value;

    Role(int valueArg) {
        value = valueArg;
    }

    public static Role getByID(int id) {
        for (Role role : values()) {
            if (role.value == id) {
                return role;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public int getValue() {
        return value;
    }
}
