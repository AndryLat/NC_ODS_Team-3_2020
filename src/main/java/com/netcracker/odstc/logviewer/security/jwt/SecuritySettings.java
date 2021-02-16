package com.netcracker.odstc.logviewer.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:security.properties")
public class SecuritySettings {

    @Value("${security.secret_key}")
    private String secretKey;

    @Value("${security.header}")
    private String header;

    @Value("${security.prefix}")
    private String prefix;

    @Value("${security.expiration_time}")
    private long expirationTime;

    @Value("${security.expiration_time.reset_password}")
    private long expirationTimeResetPassword;

    public String getSecretKey() {
        return secretKey;
    }

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public long getExpirationTimeResetPassword() {
        return expirationTimeResetPassword;
    }
}
