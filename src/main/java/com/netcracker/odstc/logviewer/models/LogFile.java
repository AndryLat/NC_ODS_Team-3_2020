package com.netcracker.odstc.logviewer.models;

import java.util.ArrayList;
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
        this.logList = new ArrayList<>();
    }

    public LogFile(long id, String name, Date lastUpdate, int lastRow, Directory parentDirectory) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.lastRow = lastRow;
        this.parentDirectory = parentDirectory;
        this.logList = new ArrayList<>();
    }

    public LogFile(String name, Date lastUpdate, int lastRow, Directory parentDirectory) {
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.lastRow = lastRow;
        this.parentDirectory = parentDirectory;
        this.logList = new ArrayList<>();
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
        boolean exist = false;
        for (LogFile l : parentDirectory.getLogFileList()) {
            exist = l.equals(this);
        }
        if (!exist) parentDirectory.addLogFile(this);
        this.parentDirectory = parentDirectory;
    }

    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    public boolean addLog(Log log) {
        for (Log l : logList) {
            if (l.equals(log)) return false;
        }
        logList.add(log);
        log.setParentFile(this);
        return true;
    }

    public String getParentTree() {
        return "User: " + this.getParentDirectory().getParentServer().getParentUser().getName()
                + "\n Server: " + this.getParentDirectory().getParentServer().getName()
                + "\n Directory: " + this.getParentDirectory().getName();
    }
}
