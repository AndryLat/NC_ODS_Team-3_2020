package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.lists.Role;

public class User {
    private long id;
    private String email;
    private String login;
    private String password;
    private Role role;

    public User() {
    }

    public User(long id, String email, String login, String password, Role role) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
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
}
