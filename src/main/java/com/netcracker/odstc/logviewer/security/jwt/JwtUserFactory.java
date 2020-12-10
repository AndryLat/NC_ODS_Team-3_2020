package com.netcracker.odstc.logviewer.security.jwt;

import com.netcracker.odstc.logviewer.models.User;

public class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(User user){
        JwtUser jwtUser = new JwtUser(user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getPassword(),
                user.getRole());
        return jwtUser;
    }
}
