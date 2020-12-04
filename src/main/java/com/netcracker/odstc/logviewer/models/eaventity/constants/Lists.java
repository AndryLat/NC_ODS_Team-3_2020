package com.netcracker.odstc.logviewer.models.eaventity.constants;

import java.math.BigInteger;

/**
 * Description:
 *
 * @author Aleksanid
 * created 04.12.2020
 */
public enum Lists {
    ADMIN(1),
    USER(2),
    SSH(3),
    FTP(4),
    TRUE(5),
    FALSE(6),
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

    private BigInteger value;

    Lists(int valueArg) {
        value = BigInteger.valueOf(valueArg);
    }

    public BigInteger getValue() {
        return value;
    }
}
