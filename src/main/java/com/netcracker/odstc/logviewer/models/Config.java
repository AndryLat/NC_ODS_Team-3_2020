package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Config extends EAVObject {
    private static Config instance = null;
    private final SimpleDateFormat format;
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public Config() {
        super();
        format = new SimpleDateFormat();
        format.applyPattern(DATE_PATTERN);
        setObjectTypeId(ObjectTypes.CONFIG.getObjectTypeID());
    }

    public Config(BigInteger objectId) {
        super(objectId);
        format = new SimpleDateFormat();
        format.applyPattern(DATE_PATTERN);
        setObjectTypeId(ObjectTypes.CONFIG.getObjectTypeID());
    }

    public static Config getInstance() {
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

    public void setStorageLogPeriod(String storageLogPeriod) throws ParseException {
        setAttributeDateValue(Attributes.STORAGE_PERIOD_OT_CONFIG.getAttrId(), format.parse(storageLogPeriod));
    }

    public void setStorageLogPeriod(Date storageLogPeriod) {
        setAttributeDateValue(Attributes.STORAGE_PERIOD_OT_CONFIG.getAttrId(), storageLogPeriod);
    }

    public Date getDirectoryActivityPeriod() {
        return getAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD_OT_CONFIG.getAttrId());
    }

    public void setDirectoryActivityPeriod(String directoryActivityPeriod) throws ParseException {
        setAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), format.parse(directoryActivityPeriod));
    }

    public void setDirectoryActivityPeriod(Date directoryActivityPeriod){
        setAttributeDateValue(Attributes.DIRECTORY_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), directoryActivityPeriod);
    }

    public Date getServerActivityPeriod() {
        return getAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId());
    }

    public void setServerActivityPeriod(String serverActivityPeriod) throws ParseException {
        setAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), format.parse(serverActivityPeriod));
    }

    public void setServerActivityPeriod(Date serverActivityPeriod){
        setAttributeDateValue(Attributes.SERVER_ACTIVITY_PERIOD_OT_CONFIG.getAttrId(), serverActivityPeriod);
    }
}
