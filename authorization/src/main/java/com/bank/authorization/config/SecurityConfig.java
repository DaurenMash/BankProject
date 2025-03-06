package com.bank.authorization.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final int BEARER_PREFIX_LENGTH = 7; // Длина префикса "Bearer"

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/swagger-ui.html", "/v2/api-docs", "/configuration/ui",
                                "/swagger-resources/**", "/configuration/security", "/webjars/**", "/auth/**")
                        .permitAll()
                        .requestMatchers("/api/users").authenticated() // Явно указываем, что /api/users требует аутентификации
                        .anyRequest().authenticated()
                )
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        final AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    /**
     * Фильтр для авторизации по JWT
     */
    private class JwtAuthorizationFilter extends OncePerRequestFilter {
        private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            final String requestURI = request.getRequestURI();
            logger.debug("Processing request to: " + requestURI);

            if (requestURI.endsWith("/auth/login")) {
                logger.debug("Skipping JWT filter for /auth/login");
                filterChain.doFilter(request, response);
                return;
            }

            final String authorizationHeader = request.getHeader("Authorization");
            logger.debug("Authorization header: " + authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                final String token = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
                logger.debug("JWT token: " + token);

                try {
                    final Claims claims = Jwts.parser()
                            .setSigningKey(secretKey.getBytes())
                            .parseClaimsJws(token)
                            .getBody();

                    if (claims != null && claims.getSubject() != null) {
                        logger.debug("JWT token is valid. Username: " + claims.getSubject());
                        request.setAttribute("username", claims.getSubject());
                    } else {
                        logger.error("JWT token is invalid. No subject found.");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                        response.getWriter().write("JWT token is invalid. No subject found.");
                        return;
                    }
                } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException |
                         IllegalArgumentException e) {
                    logger.error("Invalid or expired JWT token", e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.getWriter().write("Invalid or expired JWT token");
                    return;
                }
            } else {
                logger.error("No valid Authorization header found");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("No valid Authorization header found");
                return;
            }

            filterChain.doFilter(request, response);
        }
    }
}