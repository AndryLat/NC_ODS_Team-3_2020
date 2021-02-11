package com.netcracker.odstc.logviewer.config;

import com.netcracker.odstc.logviewer.security.UserDetailsServiceImpl;
import com.netcracker.odstc.logviewer.security.jwt.JwtAuthenticationFilter;
import com.netcracker.odstc.logviewer.security.jwt.JwtAuthorizationFilter;
import com.netcracker.odstc.logviewer.security.jwt.SecuritySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String GENERAL_ENDPOINT = "/*";
    private static final String ASSETS_ENDPOINT = "/assets/*";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String USER_RESET_ENDPOINT = "/api/user/resetPassword/**";
    private static final String USER_CHANGE_PASSWORD_ENDPOINT = "/api/user/changePassword**";
    private static final String USER_UPDATE_PASSWORD_ENDPOINT = "/api/user/updatePassword";
    public static final String USER_GET_INFO_ENDPOINT = "/api/user/getInfo";
    public static final String USER_CHECK_PASSWORD_ENDPOINT = "/api/user/checkPassword";
    private static final String ADMIN_ENDPOINT = "/api/user/*";

    private final UserDetailsServiceImpl userDetailsService;
    private final SecuritySettings securitySettings;

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, SecuritySettings securitySettings) {
        this.userDetailsService = userDetailsService;
        this.securitySettings = securitySettings;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers(USER_RESET_ENDPOINT, USER_UPDATE_PASSWORD_ENDPOINT);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers("/ws/**").permitAll()// Will be secured through WebSocketSecurity
                .antMatchers(LOGIN_ENDPOINT, GENERAL_ENDPOINT, USER_RESET_ENDPOINT, USER_CHANGE_PASSWORD_ENDPOINT, USER_UPDATE_PASSWORD_ENDPOINT, USER_GET_INFO_ENDPOINT, USER_CHECK_PASSWORD_ENDPOINT, ASSETS_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT).access("hasAuthority('ADMIN')")
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), securitySettings))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), securitySettings))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
