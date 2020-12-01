package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.lists.Protocol;

import java.util.Date;
import java.util.List;

public class Server {
    private long id;
    private String ip;
    private String login;
    private String password;
    private Protocol protocol;
    private int port;
    private boolean isActive;
    private Date lastAccessByJob;
    private Date lastAccessByUser;
    private User parentUser;
    private String name;
    private List<Directory> directoryList;

    public Server() {
    }

    public Server(long id, String ip, String login, String password, Protocol protocol, int port, User parentUser) {
        this.id = id;
        this.ip = ip;
        this.login = login;
        this.password = password;
        this.protocol = protocol;
        this.port = port;
        this.isActive = true;
        this.lastAccessByJob = new Date();
        this.lastAccessByUser = new Date();
        this.parentUser = parentUser;
    }

    public Server(String ip, String login, String password, Protocol protocol, int port, User parentUser) {
        this.ip = ip;
        this.login = login;
        this.password = password;
        this.protocol = protocol;
        this.port = port;
        this.isActive = true;
        this.lastAccessByJob = new Date();
        this.lastAccessByUser = new Date();
        this.parentUser = parentUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getLastAccessByJob() {
        return lastAccessByJob;
    }

    public void setLastAccessByJob(Date lastAccessByJob) {
        this.lastAccessByJob = lastAccessByJob;
    }

    public Date getLastAccessByUser() {
        return lastAccessByUser;
    }

    public void setLastAccessByUser(Date lastAccessByUser) {
        this.lastAccessByUser = lastAccessByUser;
    }

    public User getParentUser() {
        return parentUser;
    }

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Directory> getDirectoryList() {
        return directoryList;
    }

    public void setDirectoryList(List<Directory> directoryList) {
        this.directoryList = directoryList;
    }
}
