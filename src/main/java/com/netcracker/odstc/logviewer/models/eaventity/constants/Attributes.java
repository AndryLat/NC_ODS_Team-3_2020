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
    IS_ACTIVE_OT_DIRECTORY(15),// Заменить наименования такой шаблон
    LAST_EXISTENCE_CHECK(16),
    LAST_ACCESS_BY_USER_OT_DIRECTORY(17),
    NAME(18),
    LAST_UPDATE(19),
    LAST_ROW(20),
    TEXT(21),
    LEVEL(22),
    CREATION_DATE(23),
    CHANGES_POLLING_PERIOD(24),
    ACTIVITY_POLLING_PERIOD(25),
    STORAGE_PERIOD(26),
    DIRECTORY_ACTIVITY_PERIOD(27),
    SERVER_ACTIVITY_PERIOD_OT_CONFIG(28);


    private BigInteger attrId;

    Attributes(int valueArg) {
        attrId = BigInteger.valueOf(valueArg);
    }// Заменить на BigInteger

    public BigInteger getAttrId() {
        return attrId;
    }
}
