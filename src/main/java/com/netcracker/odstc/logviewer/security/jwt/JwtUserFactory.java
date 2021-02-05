package com.netcracker.odstc.logviewer.security.jwt;

import com.netcracker.odstc.logviewer.models.User;

public class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        JwtUser jwtUser;
        try {
            jwtUser = new JwtUser(user.getObjectId(),
                    user.getLogin(),
                    user.getPassword(),
                    user.getRole().name());
        } catch (Exception exp) {
            jwtUser = new JwtUser(user.getObjectId(),
                    user.getLogin(),
                    user.getPassword());
        }
        return jwtUser;
    }
}
