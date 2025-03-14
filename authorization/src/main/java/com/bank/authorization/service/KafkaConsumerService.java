package com.bank.authorization.service;

import com.bank.authorization.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UserService userService;
    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserCommandHandler userCommandHandler;

    @KafkaListener(topics = "auth.login", groupId = "authorization-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleLoginRequest(AuthRequest authRequest) {
        log.info("Received LOGIN request for user: {}", authRequest.getProfileId());

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(authRequest.getRequestId()); // Передаём requestId от клиента

        try {
            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getProfileId(), authRequest.getPassword())
            );

            final String jwt = jwtTokenProvider.generateToken(String.valueOf(authRequest.getProfileId()), authenticate.getAuthorities());

            final AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setAuthorities(authenticate.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            response.setData(authResponse);
            response.setSuccess(true);
            response.setMessage("Login successful");
            log.info("LOGIN request processed successfully for user: {}", authRequest.getProfileId());
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", authRequest.getProfileId(), e);
            response.setSuccess(false);
            response.setMessage("Invalid username or password");
        } catch (Exception e) {
            log.error("Error processing LOGIN request: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error processing login request");
        }

        kafkaTemplate.send("auth.login.response", response);
    }
    // Вынес создание пользователя в отдельный класс через бин, чтобы на нём работало AOP
    @KafkaListener(topics = "user.create", groupId = "authorization-group")
    public void consumeCreateUserRequest(KafkaRequest request) {
        KafkaResponse response = userCommandHandler.handleCreateUser(request); // Вызываем через новый бин
        kafkaTemplate.send("user.create.response", response);
    }

    // Вынес изменение пользователя в отдельный класс через бин, чтобы на нём работало AOP
    @KafkaListener(topics = "user.update", groupId = "authorization-group")
    public void consumeUpdateUserRequest(KafkaRequest request) {
        KafkaResponse response = userCommandHandler.handleUpdateUser(request); // Вызываем через новый бин
        kafkaTemplate.send("user.update.response", response);
    }


    @KafkaListener(topics = "user.delete", groupId = "authorization-group")
    public void handleDeleteUser(KafkaRequest request) {
        log.info("Received DELETE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId()); // Передаем правильный requestId

        try {

            validateTokenAndCheckPermissions(request.getJwtToken(), "ROLE_ADMIN");

            Long userId = Long.valueOf(request.getPayload().toString());

            userService.deleteById(userId);
            response.setSuccess(true);
            response.setMessage("User deleted successfully");
            log.info("DELETE_USER request processed successfully for userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing DELETE_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error deleting user: " + e.getMessage());
        }

        kafkaTemplate.send("user.delete.response", response);
    }

    @KafkaListener(topics = "user.get", groupId = "authorization-group")
    public void handleGetUser(KafkaRequest request) {
        log.info("Received GET_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId()); // Передаем правильный requestId

        try {

            validateTokenAndCheckPermissions(request.getJwtToken(), "ROLE_ADMIN");

            Long userId = Long.valueOf(request.getPayload().toString());

            final UserDto user = userService.getUserById(userId);
            response.setData(user);
            response.setSuccess(true);
            response.setMessage("User fetched successfully");
            log.info("GET_USER request processed successfully for userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing GET_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error fetching user: " + e.getMessage());
        }

        kafkaTemplate.send("user.get.response", response);
    }

    @KafkaListener(topics = "user.get.all", groupId = "authorization-group")
    public void handleGetAllUsers(KafkaRequest request) {
        log.info("Received GET_ALL_USERS request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId()); // Передаем правильный requestId

        try {

            validateTokenAndCheckPermissions(request.getJwtToken(), "ROLE_ADMIN");

            response.setData(userService.getAllUsers());
            response.setSuccess(true);
            response.setMessage("Users fetched successfully");
            log.info("GET_ALL_USERS request processed successfully");
        } catch (Exception e) {
            log.error("Error processing GET_ALL_USERS request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error fetching users: " + e.getMessage());
        }

        kafkaTemplate.send("user.get.all.response", response);
    }
    private void validateTokenAndCheckPermissions(String jwtToken, String requiredRole) {

        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new SecurityException("Invalid JWT token");
        }

        List<String> authorities = jwtTokenProvider.getAuthoritiesFromToken(jwtToken);

        if (!authorities.contains(requiredRole)) {
            throw new SecurityException("User does not have permission to perform this operation");
        }
    }
}