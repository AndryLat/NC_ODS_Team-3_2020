package com.netcracker.odstc.logviewer.models;

import java.util.Date;

public class Config {
    private short changesPollingPeriod; //milliseconds
    private short activityPollingPeriod; //milliseconds
    private Date storageLogPeriod;
    private Date directoryActivityPeriod;
    private static Config instance = null;

    public Config() {
    }

    public Config(short changesPollingPeriod, short activityPollingPeriod, Date storageLogPeriod, Date directoryActivityPeriod) {
        this.changesPollingPeriod = changesPollingPeriod;
        this.activityPollingPeriod = activityPollingPeriod;
        this.storageLogPeriod = storageLogPeriod;
        this.directoryActivityPeriod = directoryActivityPeriod;
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();

        return instance;
    }

    public short getChangesPollingPeriod() {
        return changesPollingPeriod;
    }

    public void setChangesPollingPeriod(short changesPollingPeriod) {
        this.changesPollingPeriod = changesPollingPeriod;
    }

    public short getActivityPollingPeriod() {
        return activityPollingPeriod;
    }

    public void setActivityPollingPeriod(short activityPollingPeriod) {
        this.activityPollingPeriod = activityPollingPeriod;
    }

    public Date getStorageLogPeriod() {
        return storageLogPeriod;
    }

    public void setStorageLogPeriod(Date storageLogPeriod) {
        this.storageLogPeriod = storageLogPeriod;
    }

    public Date getDirectoryActivityPeriod() {
        return directoryActivityPeriod;
    }

    public void setDirectoryActivityPeriod(Date directoryActivityPeriod) {
        this.directoryActivityPeriod = directoryActivityPeriod;
    }
}
