package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.lists.Protocol;

import java.util.Date;

public class Server {
    private long id;
    private String ip;
    private String login;
    private String password;
    private Protocol protocol;
    private int port;
    private boolean isActive;
    private Date lastAccessByJod;
    private Date lastAccessByUser;
    private User parentUser;

    public Server() {
    }

    public Server(long id, String ip, String login, String password, Protocol protocol, int port, boolean isActive, Date lastAccessByJod, Date lastAccessByUser, User parentUser) {
        this.id = id;
        this.ip = ip;
        this.login = login;
        this.password = password;
        this.protocol = protocol;
        this.port = port;
        this.isActive = isActive;
        this.lastAccessByJod = lastAccessByJod;
        this.lastAccessByUser = lastAccessByUser;
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

    public Date getLastAccessByJod() {
        return lastAccessByJod;
    }

    public void setLastAccessByJod(Date lastAccessByJod) {
        this.lastAccessByJod = lastAccessByJod;
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
}
