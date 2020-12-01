package com.netcracker.odstc.logviewer.models;

import java.util.Date;
import java.util.List;

public class LogFile {
    private long id;
    private String name;
    private Date lastUpdate;
    private int lastRow;
    private Directory parentDirectory;
    private List<Log> logList;

    public LogFile() {
    }

    public LogFile(long id, String name, Date lastUpdate, int lastRow, Directory parentDirectory) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.lastRow = lastRow;
        this.parentDirectory = parentDirectory;
    }

    public LogFile(String name, Date lastUpdate, int lastRow, Directory parentDirectory) {
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.lastRow = lastRow;
        this.parentDirectory = parentDirectory;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getLastRow() {
        return lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }
}
