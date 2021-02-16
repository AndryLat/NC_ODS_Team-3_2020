package com.netcracker.odstc.logviewer.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final SecuritySettings securitySettings;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecuritySettings securitySettings) {
        this.authenticationManager = authenticationManager;
        this.securitySettings = securitySettings;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            JwtUser jwtUser = JwtUserFactory.create(user);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtUser.getUsername(),
                            jwtUser.getPassword(),
                            jwtUser.getAuthorities()
                    )
            );
        } catch (IOException e) {
            logger.error("Failed attempt Authentication:", e);
            throw new RuntimeException("Failed attempt Authentication:", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String roleUser = ((JwtUser) authResult.getPrincipal()).getAuthorities().toArray()[0].toString();

        String token = JWT.create()
                .withSubject(((JwtUser) authResult.getPrincipal()).getUsername())
                .withClaim("Role", roleUser)
                .withExpiresAt(new Date(System.currentTimeMillis() + securitySettings.getExpirationTime()))
                .sign(Algorithm.HMAC256(securitySettings.getSecretKey().getBytes()));
        response.addHeader(securitySettings.getHeader(), securitySettings.getPrefix() + token);
    }
}
