package com.netcracker.odstc.logviewer.models.eaventity.constants;

import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;

import java.math.BigInteger;

public enum ObjectTypes {
    USER(BigInteger.valueOf(1), User.class),
    SERVER(BigInteger.valueOf(2), Server.class),
    DIRECTORY(BigInteger.valueOf(3), Directory.class),
    LOGFILE(BigInteger.valueOf(4), LogFile.class),
    LOG(BigInteger.valueOf(5), Log.class),
    CONFIG(BigInteger.valueOf(6), Config.class);

    private final BigInteger objectTypeID;
    private final Class<?> objectClass;

    ObjectTypes(BigInteger valueArg, Class<?> objectClass) {
        objectTypeID = valueArg;
        this.objectClass = objectClass;
    }

    public static ObjectTypes getObjectTypesByObjectTypeId(BigInteger objectTypeID) {
        for (ObjectTypes objectType : values()) {
            if (objectType.objectTypeID.equals(objectTypeID)) {
                return objectType;
            }
        }
        throw new IllegalArgumentException("ObjectTypes do not contain value with id: " + objectTypeID);
    }

    public BigInteger getObjectTypeID() {
        return objectTypeID;
    }

    public Class getObjectClass() {
        return objectClass;
    }
}
