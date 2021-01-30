package com.netcracker.odstc.logviewer.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.netcracker.odstc.logviewer.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;

import static com.netcracker.odstc.logviewer.security.jwt.SecurityConstants.EXPIRATION_TIME_RESET_PASSWORD;
import static com.netcracker.odstc.logviewer.security.jwt.SecurityConstants.SECRET_KEY;

@Service
public class SecurityService {

    private final Logger logger = LogManager.getLogger(SecurityService.class.getName());

    private final UserService userService;

    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    public String createPasswordResetTokenForUser(User user) {
        String token = JWT.create()
                .withSubject(user.getLogin())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_RESET_PASSWORD))
                .sign(Algorithm.HMAC256(SECRET_KEY.getBytes()));
        return token;
    }

    public String getLogin(String token,BigInteger id){
        if (!validatePasswordResetToken(token, id)) {
            logger.error("Password reset is not available.");
            throw new IllegalArgumentException("Password reset is not available.");
        }
        return getLoginUserFromToken(token);
    }

    public boolean validatePasswordResetToken(String token, BigInteger id) {
        if (token != null) {
            try {
                String userLogin = JWT.require(Algorithm.HMAC256(SECRET_KEY.getBytes()))
                        .build()
                        .verify(token)
                        .getSubject();
                if (userLogin != null) {
                    User user = userService.findById(id);
                    if (userLogin.equals(user.getLogin())) {
                        return true;
                    }
                    logger.error("There are two different users in token and id.");
                }
            } catch (TokenExpiredException exp) {
                logger.error("Password reset token expired", exp);
                return false;
            }
        }
        return false;
    }

    public String getLoginUserFromToken(String token) {
        String login;
        if (token != null) {
            try {
                login = JWT.require(Algorithm.HMAC256(SECRET_KEY.getBytes()))
                        .build()
                        .verify(token)
                        .getSubject();
                if (login != null) {
                    return login;
                }
            } catch (TokenExpiredException exp) {
                logger.error("Password reset token expired", exp);
                return null;
            }
        }
        return null;
    }

    public String getAppUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://")
                .append(serverName)
                .append(":")
                .append(serverPort)
                .append(contextPath);
        return url.toString();
    }
}
