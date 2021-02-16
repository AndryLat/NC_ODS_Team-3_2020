package com.netcracker.odstc.logviewer.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final SecuritySettings securitySettings;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, SecuritySettings securitySettings) {
        super(authenticationManager);
        this.securitySettings = securitySettings;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(securitySettings.getHeader());

        if (header == null || !header.startsWith(securitySettings.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(securitySettings.getHeader());

        if (token != null) {
            String user = JWT.require(Algorithm.HMAC256(securitySettings.getSecretKey().getBytes()))
                    .build()
                    .verify(token.replace(securitySettings.getPrefix(), ""))
                    .getSubject();

            String role = JWT.require(Algorithm.HMAC256(securitySettings.getSecretKey().getBytes()))
                    .build()
                    .verify(token.replace(securitySettings.getPrefix(), ""))
                    .getClaim("Role").asString();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));

            if (user != null && role != null) {
                return new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        authorities
                );
            }
            return null;
        }
        return null;
    }
}
