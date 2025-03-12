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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SecurityConfig {

    private static final int BEARER_PREFIX_LENGTH = 7; // Длина префикса "Bearer"
    private static final String ASTERISK = "*";
    private static final String JWT_TOKEN_IS_INVALID = "JWT token is invalid. No subject found.";
    private static final String INVALID_OR_EXPIRED_JWT_TOKEN = "Invalid or expired JWT token";
    private static final String NO_VALID_AUTHORIZATION_HEADER_FOUND = "No valid Authorization header found";

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    final CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(ASTERISK)); // Разрешить все источники
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // Разрешить методы
                    config.setAllowedHeaders(List.of(ASTERISK)); // Разрешить все заголовки
                    return config;
                }))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/api/authorization/swagger-ui.html",
                                "/api/authorization/swagger-ui/**",
                                "/api/authorization/v3/api-docs/**",
                                "/api/authorization/webjars/**",
                                "/auth/**",
                                "/api/authorization/auth/**"
                        )
                        .permitAll() // Разрешаем доступ без аутентификации
                        .requestMatchers("/api/authorization/swagger-ui/index.html").permitAll()
                        .requestMatchers("/api/authorization/api/users/**").hasRole("ADMIN")
                        //.anyRequest().permitAll() // Разрешаем доступ без аутентификации для всех остальных запросов
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
        private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            final String requestURI = request.getRequestURI();
            LOGGER.debug("Processing request to: " + requestURI);

            // Шаг 1: Проверка исключений для URL
            if (isExcludedPath(requestURI)) {
                LOGGER.debug("Skipping JWT filter for Swagger UI or auth endpoints");
                filterChain.doFilter(request, response);
                return;
            }

            // Шаг 2: Обработка заголовков аутентификации
            final String jwtToken = getAndValidateJwtToken(request, response);
            if (jwtToken == null) {
                return; // Ошибка уже обработана в методе getAndValidateJwtToken
            }

            // Шаг 3: Создание объекта аутентификации
            setSecurityContext(jwtToken, request, response);

            // Передача управления следующему фильтру
            filterChain.doFilter(request, response);
        }

        // Метод для проверки исключенных путей
        private boolean isExcludedPath(String requestURI) {
            return requestURI.startsWith("/swagger-ui") ||
                    requestURI.startsWith("/v3/api-docs") ||
                    requestURI.startsWith("/webjars") ||
                    requestURI.startsWith("/auth") ||
                    requestURI.startsWith("/api/authorization/swagger-ui") ||
                    requestURI.startsWith("/api/authorization/v3/api-docs") ||
                    requestURI.startsWith("/api/authorization/webjars") ||
                    requestURI.startsWith("/api/authorization/auth");
        }

        // Метод для получения и валидации JWT-токена
        private String getAndValidateJwtToken(HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            final String authorizationHeader = request.getHeader("Authorization");
            LOGGER.debug("Authorization header: " + authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                final String token = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
                LOGGER.debug("JWT token: " + token);

                try {
                    final Claims claims = Jwts.parser()
                            .setSigningKey(secretKey.getBytes())
                            .parseClaimsJws(token)
                            .getBody();

                    if (claims != null && claims.getSubject() != null) {
                        LOGGER.debug("JWT token is valid. Username: " + claims.getSubject());
                        return token;
                    } else {
                        LOGGER.error(JWT_TOKEN_IS_INVALID);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                        response.getWriter().write(JWT_TOKEN_IS_INVALID);
                        return null;
                    }
                } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException |
                         IllegalArgumentException | IOException e) {
                    LOGGER.error(INVALID_OR_EXPIRED_JWT_TOKEN, e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.getWriter().write(INVALID_OR_EXPIRED_JWT_TOKEN);
                    return null;
                }
            } else {
                LOGGER.error(NO_VALID_AUTHORIZATION_HEADER_FOUND);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write(NO_VALID_AUTHORIZATION_HEADER_FOUND);
                return null;
            }
        }

        // Метод для установки контекста безопасности
        private void setSecurityContext(String jwtToken, HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            try {
                final Claims claims = Jwts.parser()
                        .setSigningKey(secretKey.getBytes())
                        .parseClaimsJws(jwtToken)
                        .getBody();

                if (claims != null && claims.getSubject() != null) {
                    final String username = claims.getSubject();
                    final List<GrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);
                    final UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("username", username);
                }
            } catch (Exception e) {
                LOGGER.error(INVALID_OR_EXPIRED_JWT_TOKEN, e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write(INVALID_OR_EXPIRED_JWT_TOKEN);
            }
        }

        private List<GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
            final List<GrantedAuthority> authorities = new ArrayList<>();

            // Предполагаем, что authoritiesList — это List<String>
            final List<String> roles = claims.get("authorities", List.class);
            LOGGER.debug("Authorities from claims: " + roles);

            if (roles != null) {
                for (String role : roles) {
                    LOGGER.debug("Extracted role: " + role);
                    if (role != null) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }
            }

            LOGGER.debug("Final authorities: " + authorities);
            return authorities;
        }
    }
}
