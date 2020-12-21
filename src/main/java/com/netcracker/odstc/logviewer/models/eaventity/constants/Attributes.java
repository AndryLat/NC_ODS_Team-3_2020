package com.netcracker.odstc.logviewer.models.eaventity.constants;

import java.math.BigInteger;

public enum Attributes {
    EMAIL_OT_USER(BigInteger.valueOf(1)),
    LOGIN_OT_USER(BigInteger.valueOf(2)),
    PASSWORD_OT_USER(BigInteger.valueOf(3)),
    ROLE_OT_USER(BigInteger.valueOf(4)),
    CREATED_OT_USER(BigInteger.valueOf(5)),
    IP_ADDRESS_OT_SERVER(BigInteger.valueOf(6)),
    LOGIN_OT_SERVER(BigInteger.valueOf(7)),
    PASSWORD_OT_SERVER(BigInteger.valueOf(8)),
    PROTOCOL_OT_SERVER(BigInteger.valueOf(9)),
    PORT_OT_SERVER(BigInteger.valueOf(10)),
    IS_ENABLED_OT_SERVER(BigInteger.valueOf(11)),
    IS_CAN_CONNECT_OT_SERVER(BigInteger.valueOf(12)),
    LAST_ACCESS_BY_JOB_OT_SERVER(BigInteger.valueOf(13)),
    LAST_ACCESS_BY_USER_OT_SERVER(BigInteger.valueOf(14)),
    PATH_OT_DIRECTORY(BigInteger.valueOf(15)),
    IS_ENABLED_OT_DIRECTORY(BigInteger.valueOf(16)),
    IS_CAN_CONNECT_OT_DIRECTORY(BigInteger.valueOf(17)),
    LAST_EXISTENCE_CHECK_OT_DIRECTORY(BigInteger.valueOf(18)),
    LAST_ACCESS_BY_USER_OT_DIRECTORY(BigInteger.valueOf(19)),
    NAME_OT_LOGFILE(BigInteger.valueOf(20)),
    LAST_UPDATE_OT_LOGFILE(BigInteger.valueOf(21)),
    LAST_ROW_OT_LOGFILE(BigInteger.valueOf(22)),
    TEXT_OT_LOG(BigInteger.valueOf(23)),
    LEVEL_OT_LOG(BigInteger.valueOf(24)),
    CREATION_DATE_OT_LOG(BigInteger.valueOf(25)),
    CHANGES_POLLING_PERIOD_OT_CONFIG(BigInteger.valueOf(26)),
    ACTIVITY_POLLING_PERIOD_OT_CONFIG(BigInteger.valueOf(27)),
    STORAGE_PERIOD_OT_CONFIG(BigInteger.valueOf(28)),
    DIRECTORY_ACTIVITY_PERIOD_OT_CONFIG(BigInteger.valueOf(29)),
    SERVER_ACTIVITY_PERIOD_OT_CONFIG(BigInteger.valueOf(30));

    private BigInteger attrId;

    Attributes(BigInteger valueArg) {
        attrId = valueArg;
    }

    public BigInteger getAttrId() {
        return attrId;
    }
}
