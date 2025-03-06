package com.bank.authorization.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bank.authorization.dto.AuthRequest;
import com.bank.authorization.dto.AuthResponse;
import com.bank.authorization.repository.UserRepository;
import com.bank.authorization.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private static final String AUTHORITY = "authorities";

    // Готовим время жизни токена
    private static final Date TOKEN_LIFETIME = new Date(System.currentTimeMillis() + 3600000); // Срок действия - 1 час

    // Берём из настроек секретный ключ для подписи JWT-токена
    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            logger.debug("Attempting to authenticate user: " + authRequest.getUsername());
            final Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                            authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);

            final Collection<? extends GrantedAuthority> authorities = authenticate.getAuthorities();
            final String jwt = generateJwtToken(authRequest.getUsername(), authorities);

            final Map<String, Object> data = new HashMap<>();
            data.put("jwt", jwt);
            data.put(AUTHORITY, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

            return ResponseEntity.ok(new AuthResponse(data));
        } catch (BadCredentialsException ex) {
            logger.error("Authentication failed for user: " + authRequest.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    private String generateJwtToken(String username, Collection<? extends GrantedAuthority> authorities) {

        return Jwts.builder().setSubject(username).claim(AUTHORITY, authorities.stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(TOKEN_LIFETIME)
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();
    }
}
