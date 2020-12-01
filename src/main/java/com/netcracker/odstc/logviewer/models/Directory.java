package com.netcracker.odstc.logviewer.models;

import java.util.Date;
import java.util.List;

public class Directory {
    private long id;
    private String path;
    private long size;
    private boolean isActive;
    private Date lastExistenceCheck;
    private Server parentServer;
    private String name;
    private List<LogFile> logFileList;

    public Directory() {
    }

    public Directory(long id, String path, long size, boolean isActive, Date lastExistenceCheck, Server parentServer) {
        this.id = id;
        this.path = path;
        this.size = size;
        this.isActive = isActive;
        this.lastExistenceCheck = lastExistenceCheck;
        this.parentServer = parentServer;
    }

    public Directory(String path, long size, boolean isActive, Date lastExistenceCheck, Server parentServer) {
        this.path = path;
        this.size = size;
        this.isActive = isActive;
        this.lastExistenceCheck = lastExistenceCheck;
        this.parentServer = parentServer;
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

    public Server getParentServer() {
        return parentServer;
    }

    public void setParentServer(Server parentServer) {
        this.parentServer = parentServer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LogFile> getLogFileList() {
        return logFileList;
    }

    public void setLogFileList(List<LogFile> logFileList) {
        this.logFileList = logFileList;
    }
}
