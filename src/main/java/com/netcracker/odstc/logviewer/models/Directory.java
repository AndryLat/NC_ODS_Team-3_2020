package com.netcracker.odstc.logviewer.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Directory {
    private long id;
    private String path;
    private long size;
    private boolean isActive;
    private Date lastExistenceCheck;
    private Date lastAccessByUser;
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
        this.logFileList = new ArrayList<>();
    }

    public Directory(String path, long size, boolean isActive, Date lastExistenceCheck, Server parentServer) {
        this.path = path;
        this.size = size;
        this.isActive = isActive;
        this.lastExistenceCheck = lastExistenceCheck;
        this.parentServer = parentServer;
        this.logFileList = new ArrayList<>();
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

    public Date getLastAccessByUser() {
        return lastAccessByUser;
    }

    public void setLastAccessByUser(Date lastAccessByUser) {
        this.lastAccessByUser = lastAccessByUser;
    }

    public Server getParentServer() {
        return parentServer;
    }

    public void setParentServer(Server parentServer) {
        boolean exist = false;
        for (Directory d : parentServer.getDirectoryList()) {
            exist = d.equals(this);
        }
        if (!exist) parentServer.addDirectory(this);
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

    public boolean addLogFile(LogFile logFile) {
        for (LogFile l : logFileList) {
            if (l.equals(logFile)) return false;
        }
        logFileList.add(logFile);
        logFile.setParentDirectory(this);
        return true;
    }

    public String getParentTree() {
        return "User: " + this.getParentServer().getParentUser().getName()
                + "\n Server: " + this.getParentServer().getName();
    }
}
