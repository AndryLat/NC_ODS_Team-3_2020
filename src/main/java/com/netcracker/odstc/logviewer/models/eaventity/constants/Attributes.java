package com.netcracker.odstc.logviewer.models.eaventity.constants;

import java.math.BigInteger;

/**
 * Description:
 *
 * @author Aleksanid
 * created 04.12.2020
 */
public enum  Attributes {
    EMAIL(1),
    LOGIN(2),
    PASSWORD(3),
    ROLE(4),
    IP_ADDRESS(6),
    SERVER_LOGIN(7),
    SERVER_PASSWORD(8),
    PROTOCOL(9),
    PORT(10),
    IS_ACTIVE(11),
    LAST_ACCESS_BY_JOB(12),
    LAST_ACCESS_BY_USER(13),
    PATH(14),
    SIZE_OT_DIRECTORY(15),// Заменить наименования
    IS_ACTIVE_OT_DIRECTORY(16),
    LAST_EXISTENCE_CHECK(17),
    LAST_ACCESS_BY_USER_OT_DIRECTORY(18),
    NAME(19),
    LAST_UPDATE(20),
    LAST_ROW(21),
    TEXT(22),
    LEVEL(23),
    CREATION_DATE(24),
    CHANGES_POLLING_PERIOD(25),
    ACTIVITY_POLLING_PERIOD(26),
    STORAGE_PERIOD(27),
    DIRECTORY_ACTIVITY_PERIOD(28);


    private BigInteger attrId;

    Attributes(int valueArg) {
        attrId = BigInteger.valueOf(valueArg);
    }// Заменить на BigInteger

    public BigInteger getAttrId() {
        return attrId;
    }
}
