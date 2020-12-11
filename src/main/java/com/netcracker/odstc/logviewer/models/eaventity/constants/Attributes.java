package com.netcracker.odstc.logviewer.models.eaventity.constants;

import java.math.BigInteger;

public enum  Attributes {
    EMAIL_OT_USER (BigInteger.valueOf(1)),
    LOGIN_OT_USER(BigInteger.valueOf(2)),
    PASSWORD_OT_USER(BigInteger.valueOf(3)),
    ROLE_OT_USER(BigInteger.valueOf(4)),
    IP_ADDRESS_OT_SERVER(BigInteger.valueOf(6)),
    LOGIN_OT_SERVER(BigInteger.valueOf(7)),
    PASSWORD_OT_SERVER(BigInteger.valueOf(8)),
    PROTOCOL_OT_SERVER(BigInteger.valueOf(9)),
    PORT_OT_SERVER(BigInteger.valueOf(10)),
    IS_ACTIVE_OT_SERVER(BigInteger.valueOf(11)),
    LAST_ACCESS_BY_JOB_OT_SERVER(BigInteger.valueOf(12)),
    LAST_ACCESS_BY_USER_OT_SERVER(BigInteger.valueOf(13)),
    PATH_OT_DIRECTORY(BigInteger.valueOf(14)),
    IS_ACTIVE_OT_DIRECTORY(BigInteger.valueOf(15)),
    LAST_EXISTENCE_CHECK_OT_DIRECTORY(BigInteger.valueOf(16)),
    LAST_ACCESS_BY_USER_OT_DIRECTORY(BigInteger.valueOf(17)),
    NAME_OT_LOGFILE(BigInteger.valueOf(18)),
    LAST_UPDATE_OT_LOGFILE(BigInteger.valueOf(19)),
    LAST_ROW_OT_LOGFILE(BigInteger.valueOf(20)),
    TEXT_OT_LOG(BigInteger.valueOf(21)),
    LEVEL_OT_LOG(BigInteger.valueOf(22)),
    CREATION_DATE_OT_LOG(BigInteger.valueOf(23)),
    CHANGES_POLLING_PERIOD_OT_CONFIG(BigInteger.valueOf(24)),
    ACTIVITY_POLLING_PERIOD_OT_CONFIG(BigInteger.valueOf(25)),
    STORAGE_PERIOD_OT_CONFIG(BigInteger.valueOf(26)),
    DIRECTORY_ACTIVITY_PERIOD_OT_CONFIG(BigInteger.valueOf(27)),
    SERVER_ACTIVITY_PERIOD_OT_CONFIG(BigInteger.valueOf(28));

    private BigInteger attrId;

    Attributes(BigInteger valueArg) {
        attrId = valueArg;
    }

    public BigInteger getAttrId() {
        return attrId;
    }
}
