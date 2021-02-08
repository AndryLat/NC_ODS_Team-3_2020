package com.netcracker.odstc.logviewer.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:security.properties")
public class SecuritySettings {

    @Value("${security.secret_key}")
    private String secret_key;

    @Value("${security.header}")
    private String header;

    @Value("${security.prefix}")
    private String prefix;

    @Value("${security.expiration_time}")
    private long expiration_time;

    @Value("${security.expiration_time.reset_password}")
    private long expiration_time_reset_password;

    public String getSecret_key() {
        return secret_key;
    }

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public long getExpiration_time() {
        return expiration_time;
    }

    public long getExpiration_time_reset_password() {
        return expiration_time_reset_password;
    }
}
