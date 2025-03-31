package com.bank.authorization.handler;

import com.bank.authorization.dto.AuthRequest;
import com.bank.authorization.dto.AuthResponse;
import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import com.bank.authorization.utils.JwtTokenUtil;
import com.bank.authorization.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;
    private final ResponseFactory responseFactory;

    public void handleLogin(AuthRequest request) {
        KafkaResponse response;
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getProfileId(), request.getPassword())
            );

            String jwt = jwtTokenUtil.generateToken(
                    String.valueOf(request.getProfileId()), authentication.getAuthorities()
            );

            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setAuthorities(authentication.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority())
                    .toList());

            response = responseFactory.createSuccessResponse(request.getRequestId(), "Login successful", authResponse);
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", request.getProfileId(), e);
            response = responseFactory.createErrorResponse(request.getRequestId(), "Invalid username or password");
        }

        kafkaTemplate.send("auth_login_response", response);
    }

    public void handleTokenValidation(KafkaRequest request) {
        KafkaResponse response;
        try {
            boolean isValid = jwtTokenUtil.validateToken(request.getJwtToken());
            response = responseFactory.createSuccessResponse(request.getRequestId(), "Token validation completed", isValid);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Error validating token");
        }

        kafkaTemplate.send("auth_validate_response", response);
    }
}
