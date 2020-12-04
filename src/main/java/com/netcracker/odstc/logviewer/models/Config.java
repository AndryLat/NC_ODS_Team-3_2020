package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;

import java.math.BigInteger;
import java.util.Date;

public class Config extends EAVObject {
    private short changesPollingPeriod; //milliseconds To Long
    private short activityPollingPeriod; //milliseconds To Long
    private Date storageLogPeriod;
    private Date directoryActivityPeriod;
    private static Config instance = null;

    public Config() {
        super();
    }

    public Config(BigInteger objectId){
        super(objectId);
        changesPollingPeriod = Short.parseShort(getAttributeValue(Attributes.CHANGES_POLLING_PERIOD.getAttrId()));
        activityPollingPeriod = Short.parseShort(getAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD.getAttrId()));

        storageLogPeriod = getAttributeDateValue(Attributes.STORAGE_PERIOD.getAttrId());
        directoryActivityPeriod = getAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD.getAttrId());
    }

    public Config(short changesPollingPeriod, short activityPollingPeriod, Date storageLogPeriod, Date directoryActivityPeriod) {
        super();
        this.changesPollingPeriod = changesPollingPeriod;//TODO: Добавить заполнение атрибутов EAV.
        this.activityPollingPeriod = activityPollingPeriod;
        this.storageLogPeriod = storageLogPeriod;
        this.directoryActivityPeriod = directoryActivityPeriod;
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();// Можно задать перманентный ид обьекта, когда настройки будут в Базе Данных

        return instance;
    }

    public short getChangesPollingPeriod() {
        return Short.parseShort(getAttributeValue(Attributes.CHANGES_POLLING_PERIOD.getAttrId()));
    }

    public void setChangesPollingPeriod(short changesPollingPeriod) {
        setAttributeValue(Attributes.CHANGES_POLLING_PERIOD.getAttrId(),String.valueOf(changesPollingPeriod));
    }

    public short getActivityPollingPeriod() {
        return activityPollingPeriod;
    }

    public void setActivityPollingPeriod(short activityPollingPeriod) {
        setAttributeValue(Attributes.ACTIVITY_POLLING_PERIOD.getAttrId(),String.valueOf(activityPollingPeriod));
        this.activityPollingPeriod = activityPollingPeriod;
    }

    public Date getStorageLogPeriod() {
        return storageLogPeriod;
    }

    public void setStorageLogPeriod(Date storageLogPeriod) {
        setAttributeDateValue(Attributes.STORAGE_PERIOD.getAttrId(),storageLogPeriod);
        this.storageLogPeriod = storageLogPeriod;
    }

    public Date getDirectoryActivityPeriod() {
        return directoryActivityPeriod;
    }

    public void setDirectoryActivityPeriod(Date directoryActivityPeriod) {
        setAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD.getAttrId(),directoryActivityPeriod);
        this.directoryActivityPeriod = directoryActivityPeriod;
    }
}
