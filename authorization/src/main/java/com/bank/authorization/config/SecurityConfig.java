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
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
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
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/api/authorization/swagger-ui.html",
                                "/api/authorization/swagger-ui/**", // Разрешаем все ресурсы Swagger UI
                                "/api/authorization/v3/api-docs",
                                "/api/authorization/v3/api-docs/**", // Разрешаем JSON-документацию
                                "/api/authorization/webjars/**",
                                "/api/authorization/auth/**",
                                "/auth/**"
                        )
                        .permitAll() // Разрешаем доступ без аутентификации
                        .requestMatchers("/api/authorization/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // Требуем аутентификацию для всех остальных запросов
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

            // Пропускаем запросы к Swagger UI и API-документации
            if (requestURI.startsWith("/api/authorization/swagger-ui") ||
                    requestURI.startsWith("/api/authorization/v3/api-docs") ||
                    requestURI.startsWith("/api/authorization/webjars") ||
                    requestURI.startsWith("/api/authorization/auth") ||
                    requestURI.endsWith("/auth/login")) {
                logger.debug("Skipping JWT filter for Swagger UI or auth endpoints");
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

                        // Создаем объект аутентификации
                        String username = claims.getSubject();
                        List<GrantedAuthority> authorities = extractAuthoritiesFromClaims(claims); // Извлечь роли/права из claims
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        request.setAttribute("username", username);
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


        private List<GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // Предполагаем, что authoritiesList — это List<String>
            List<String> roles = claims.get("authorities", List.class);
            logger.debug("Authorities from claims: " + roles);

            if (roles != null) {
                for (String role : roles) {
                    logger.debug("Extracted role: " + role);
                    if (role != null) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }
            }

            logger.debug("Final authorities: " + authorities);
            return authorities;
        }
    }
}