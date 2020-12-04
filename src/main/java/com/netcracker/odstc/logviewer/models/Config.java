package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;

import java.math.BigInteger;
import java.util.Date;

public class Config extends EAVObject {
    private static Config instance = null;
    public Config(long changesPollingPeriod, long activityPollingPeriod, Date storageLogPeriod, Date directoryActivityPeriod, Date serverActivityPeriod) {
        setAttributeValue(Attributes.CHANGES_POLLING_PERIOD.getAttrId(), String.valueOf(changesPollingPeriod));
        setAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD.getAttrId(), String.valueOf(activityPollingPeriod));
        setAttributeDateValue(Attributes.STORAGE_PERIOD.getAttrId(), storageLogPeriod);
        setAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD.getAttrId(), directoryActivityPeriod);
        setAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), serverActivityPeriod);
    }

    public Config() {
        super();
    }

    public Config(BigInteger objectId) {
        super(objectId);
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();// Можно задать перманентный ид обьекта, когда настройки будут в Базе Данных

        return instance;
    }

    public long getActivityPollingPeriod() {
        return Long.parseLong(getAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD.getAttrId()));
    }

    public void setActivityPollingPeriod(long activityPollingPeriod) {
        setAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD.getAttrId(), String.valueOf(activityPollingPeriod));
    }

    public long getChangesPollingPeriod() {
        return Long.parseLong(getAttributeValue(Attributes.CHANGES_POLLING_PERIOD.getAttrId()));
    }

    public void setChangesPollingPeriod(long changesPollingPeriod) {
        setAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD.getAttrId(), String.valueOf(changesPollingPeriod));
    }

    public Date getStorageLogPeriod() {
        return getAttributeDateValue(Attributes.STORAGE_PERIOD.getAttrId());
    }

    public void setStorageLogPeriod(Date storageLogPeriod) {
        setAttributeDateValue(Attributes.STORAGE_PERIOD.getAttrId(), storageLogPeriod);
    }

    public Date getDirectoryActivityPeriod() {
        return getAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD.getAttrId());
    }

    public void setDirectoryActivityPeriod(Date directoryActivityPeriod) {
        setAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD.getAttrId(), directoryActivityPeriod);
    }

    public Date getServerActivityPeriod() {
        return getAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId());
    }

    public void setServerActivityPeriod(Date serverActivityPeriod) {
        setAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), serverActivityPeriod);
    }
}
