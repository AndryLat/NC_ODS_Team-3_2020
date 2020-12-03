package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.lists.Role;

import java.util.ArrayList;
import java.util.List;

public class User {
    private long id;
    private String email;
    private String login;
    private String password;
    private Role role;
    private String name;
    private List<Server> serverList;
    private int created;

    public User() {
        this.serverList = new ArrayList<>();
    }

    public User(long id, String email, String login, String password, Role role) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
        this.serverList = new ArrayList<>();
    }

    public User(String email, String login, String password, Role role) {
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
        this.serverList = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public void setServerList(List<Server> serverList) {
        this.serverList = serverList;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public boolean addServer(Server server) {
        for (Server s : serverList) {
            if (s.equals(server)) return false;
        }
        serverList.add(server);
        server.setParentUser(this);
        return true;
    }
}
