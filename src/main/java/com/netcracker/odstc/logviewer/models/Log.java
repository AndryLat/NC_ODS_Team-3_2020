package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;

import java.math.BigInteger;
import java.util.Date;

public class Log extends EAVObject {
    private long id;
    private String text;
    private LogLevel level;
    private Date creationDate;
    private LogFile parentFile;
    private String name;

    public Log() {
    }

    public Log(long id, String text, LogLevel level, Date creationDate, LogFile parentFile) {
        this.id = id;
        this.text = text;
        this.level = level;
        this.creationDate = creationDate;
        this.parentFile = parentFile;
    }

    public Log(String text, LogLevel level, Date creationDate, LogFile parentFile) {
        this.text = text;
        this.level = level;
        this.creationDate = creationDate;
        this.parentFile = parentFile;
    }

    public Log(BigInteger id) {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public LogFile getParentFile() {
        return parentFile;
    }

    public void setParentFile(LogFile parentFile) {
        boolean exist = false;
        for (Log l : parentFile.getLogList()) {
            exist = l.equals(this);
        }
        if (!exist) parentFile.addLog(this);
        this.parentFile = parentFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentTree() {
        return "User: " + this.parentFile.getParentDirectory().getParentServer().getParentUser().getName()
                + "\n Server: " + this.parentFile.getParentDirectory().getParentServer().getName()
                + "\n Directory: " + this.parentFile.getParentDirectory().getName()
                + "\n File: " + this.parentFile.getName();
    }
}
