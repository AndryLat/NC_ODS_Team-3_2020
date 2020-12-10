package com.netcracker.odstc.logviewer.models.lists;

public enum Role {
    ADMIN(1),
    USER(2);

    private int value;

    Role(int valueArg) {
        value = valueArg;
    }

    public static Role getByID(int id){
        if(Role.ADMIN.value == id){
            return Role.ADMIN;
        }
        return Role.USER;
    }

    public int getValue() {
        return value;
    }
}
