package com.netcracker.odstc.logviewer.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtUser implements UserDetails {

    private final BigInteger id;
    private final String login;
    private final String password;
    private String role;
    private List<GrantedAuthority> authorities;

    public JwtUser(BigInteger id,
                   String login,
                   String password,
                   String role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
    }

    public JwtUser(BigInteger id,
                   String login,
                   String password) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.authorities = new ArrayList<>();
    }

    public String getRole() {
        return role;
    }

    public void setRole() {
        this.role = role;
        this.authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
