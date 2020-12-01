package com.netcracker.odstc.logviewer.models;

public class Config {
    private short changesPollingPeriod; //milliseconds
    private short activityPollingPeriod; //milliseconds
    private short storagePeriod; //milliseconds
    private static Config instance = null;

    public Config() {
    }

    public Config(short changesPollingPeriod, short activityPollingPeriod, short storagePeriod) {
        this.changesPollingPeriod = changesPollingPeriod;
        this.activityPollingPeriod = activityPollingPeriod;
        this.storagePeriod = storagePeriod;
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

    public short getStoragePeriod() {
        return storagePeriod;
    }

    public void setStoragePeriod(short storagePeriod) {
        this.storagePeriod = storagePeriod;
    }
}
