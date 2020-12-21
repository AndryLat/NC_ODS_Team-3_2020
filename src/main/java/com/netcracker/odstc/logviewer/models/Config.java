package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;

import java.math.BigInteger;
import java.util.Date;

public class Config extends EAVObject {
    private static Config instance = null;

    public Config() {
        super();
        setObjectTypeId(BigInteger.valueOf(6));
    }

    public Config(BigInteger objectId) {
        super(objectId);
        setObjectTypeId(BigInteger.valueOf(6));
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();

        return instance;
    }

    public static void setInstance(Config instance) {
        Config.instance = instance;
    }

    public long getActivityPollingPeriod() {
        return Long.parseLong(getAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD_OT_CONFIG.getAttrId()));
    }

    public void setActivityPollingPeriod(long activityPollingPeriod) {
        setAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD_OT_CONFIG.getAttrId(), String.valueOf(activityPollingPeriod));
    }

    public long getChangesPollingPeriod() {
        return Long.parseLong(getAttributeValue(Attributes.CHANGES_POLLING_PERIOD_OT_CONFIG.getAttrId()));
    }

    public void setChangesPollingPeriod(long changesPollingPeriod) {
        setAttributeValue(Attributes.CHANGES_POLLING_PERIOD_OT_CONFIG.getAttrId(), String.valueOf(changesPollingPeriod));
    }

    public Date getStorageLogPeriod() {
        return getAttributeDateValue(Attributes.STORAGE_PERIOD_OT_CONFIG.getAttrId());
    }

    public void setStorageLogPeriod(Date storageLogPeriod) {
        setAttributeDateValue(Attributes.STORAGE_PERIOD_OT_CONFIG.getAttrId(), storageLogPeriod);
    }

    public Date getDirectoryActivityPeriod() {
        return getAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD_OT_CONFIG.getAttrId());
    }

    public void setDirectoryActivityPeriod(Date directoryActivityPeriod) {
        setAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), directoryActivityPeriod);
    }

    public Date getServerActivityPeriod() {
        return getAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId());
    }

    public void setServerActivityPeriod(Date serverActivityPeriod) {
        setAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), serverActivityPeriod);
    }
}
