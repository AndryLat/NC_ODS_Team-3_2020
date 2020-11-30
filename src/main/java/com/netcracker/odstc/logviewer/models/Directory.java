package com.netcracker.odstc.logviewer.models;

import java.util.Date;

public class Directory {
    private long id;
    private String path;
    private long size;
    private boolean isActive;
    private Date lastExistenceCheck;
    private Server parent;

    public Directory() {
    }

    public Directory(long id, String path, long size, boolean isActive, Date lastExistenceCheck, Server parent) {
        this.id = id;
        this.path = path;
        this.size = size;
        this.isActive = isActive;
        this.lastExistenceCheck = lastExistenceCheck;
        this.parent = parent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getLastExistenceCheck() {
        return lastExistenceCheck;
    }

    public void setLastExistenceCheck(Date lastExistenceCheck) {
        this.lastExistenceCheck = lastExistenceCheck;
    }

    public Server getParent() {
        return parent;
    }

    public void setParent(Server parent) {
        this.parent = parent;
    }
}
