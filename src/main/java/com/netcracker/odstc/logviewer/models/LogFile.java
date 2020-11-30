package com.netcracker.odstc.logviewer.models;

import java.util.Date;

public class LogFile {
    private long id;
    private String name;
    private Date lastUpdate;
    private String lastRow;
    private Directory parent;

    public LogFile() {
    }

    public LogFile(long id, String name, Date lastUpdate, String lastRow, Directory parent) {
        this.id = id;
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.lastRow = lastRow;
        this.parent = parent;
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

    public String getLastRow() {
        return lastRow;
    }

    public void setLastRow(String lastRow) {
        this.lastRow = lastRow;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }
}
