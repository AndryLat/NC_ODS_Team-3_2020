package com.netcracker.odstc.logviewer.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.security.jwt.SecuritySettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;

@Service
public class SecurityService {

    private final Logger logger = LogManager.getLogger(SecurityService.class.getName());

    private final UserService userService;
    private final SecuritySettings securitySettings;

    public SecurityService(UserService userService, SecuritySettings securitySettings) {
        this.userService = userService;
        this.securitySettings = securitySettings;
    }

    public String createPasswordResetTokenForUser(User user) {
        return JWT.create()
                .withSubject(user.getLogin())
                .withExpiresAt(new Date(System.currentTimeMillis() + securitySettings.getExpirationTimeResetPassword()))
                .sign(Algorithm.HMAC256(securitySettings.getSecretKey().getBytes()));
    }

    public boolean validateToken(String token) {
        if (token != null) {
            try {
                JWT.require(Algorithm.HMAC256(securitySettings.getSecretKey().getBytes()))
                        .build()
                        .verify(token.replace(securitySettings.getPrefix(), ""));
                return true;
            } catch (TokenExpiredException exp) {
                logger.error("Token expired ", exp);
                return false;
            }
        }
        return false;
    }

    public boolean validatePasswordResetToken(String token, BigInteger id) {
        if (token != null) {
            try {
                String userLogin = JWT.require(Algorithm.HMAC256(securitySettings.getSecretKey().getBytes()))
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

    public String getLogin(String token, BigInteger id) {
        if (!validatePasswordResetToken(token, id)) {
            logger.error("Password reset is not available.");
            throw new IllegalArgumentException("Password reset is not available.");
        }
        return getLoginUserFromToken(token);
    }

    public String getLoginUserFromToken(String token) {
        String login;
        if (token != null) {
            try {
                login = JWT.require(Algorithm.HMAC256(securitySettings.getSecretKey().getBytes()))
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
