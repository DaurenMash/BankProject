package com.bank.authorization.config;

import io.jsonwebtoken.*;
import jakarta.servlet.*;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/swagger-ui.html", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/webjars/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }

    /**
     * Фильтр для авторизации по JWT
     */
    private class JwtAuthorizationFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7); // Удаляем префикс "Bearer "

                try {
                    Claims claims = Jwts.parser()
                            .setSigningKey("yourSecretKey".getBytes()) // Замените на реальный секретный ключ
                            .parseClaimsJws(token)
                            .getBody();

                    if (claims != null) {
                        String username = claims.getSubject();

                        if (username != null) {
                            request.setAttribute("username", username);
                        }
                    }
                } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException |
                         IllegalArgumentException e) {
                    // Логируйте или обрабатывайте исключения, связанные с неверным токеном
                    logger.error("Invalid or expired JWT token", e);
                }
            }

            filterChain.doFilter(request, response);
        }
    }

}
