package com.netcracker.odstc.logviewer.models.eaventity.constants;

import java.math.BigInteger;

public enum ObjectTypes {
    USER(BigInteger.valueOf(1)),
    SERVER(BigInteger.valueOf(2)),
    DIRECTORY(BigInteger.valueOf(3)),
    LOGFILE(BigInteger.valueOf(4)),
    LOG(BigInteger.valueOf(5)),
    CONFIG(BigInteger.valueOf(6));

    private BigInteger objectTypeID;

    ObjectTypes(BigInteger valueArg) {
        objectTypeID = valueArg;
    }

    public BigInteger getObjectTypeID() {
        return objectTypeID;
    }
}
