package com.netcracker.odstc.logviewer.models;

import java.util.Date;

public class Config {
    private Date changesPollingPeriod;
    private Date activityPollingPeriod;
    private short storagePeriod;
    private static Config instance = null;

    public Config() {
    }

    public Config(Date changesPollingPeriod, Date activityPollingPeriod, short storagePeriod) {
        this.changesPollingPeriod = changesPollingPeriod;
        this.activityPollingPeriod = activityPollingPeriod;
        this.storagePeriod = storagePeriod;
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();

        return instance;
    }

    public Date getChangesPollingPeriod() {
        return changesPollingPeriod;
    }

    public void setChangesPollingPeriod(Date changesPollingPeriod) {
        this.changesPollingPeriod = changesPollingPeriod;
    }

    public Date getActivityPollingPeriod() {
        return activityPollingPeriod;
    }

    public void setActivityPollingPeriod(Date activityPollingPeriod) {
        this.activityPollingPeriod = activityPollingPeriod;
    }

    public short getStoragePeriod() {
        return storagePeriod;
    }

    public void setStoragePeriod(short storagePeriod) {
        this.storagePeriod = storagePeriod;
    }
}
