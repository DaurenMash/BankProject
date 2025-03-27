package com.bank.authorization.handler;

import com.bank.authorization.dto.AuthRequest;
import com.bank.authorization.dto.AuthResponse;
import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.Role;
import com.bank.authorization.service.UserService;
import com.bank.authorization.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKafkaCommandListener {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;
    private final KafkaExceptionHandler kafkaExceptionHandler;

    @KafkaListener(topics = "auth.login", groupId = "authorization-group")
    public void consumeLoginRequest(AuthRequest request) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getProfileId(), request.getPassword())
            );

            final String jwt = jwtTokenUtil.generateToken(
                    String.valueOf(request.getProfileId()),
                    authenticate.getAuthorities()
            );

            final AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setAuthorities(authenticate.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            response.setData(authResponse);
            response.setSuccess(true);
            response.setMessage("Login successful");

            log.info("LOGIN request processed successfully for user: {}", request.getProfileId());
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", request.getProfileId(), e);
            response.setSuccess(false);
            response.setMessage("Invalid username or password");
        } catch (Exception e) {
            log.error("Error processing LOGIN request: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error processing login request");
            kafkaExceptionHandler.handleException(e, request.getRequestId());
        }

        kafkaTemplate.send("auth.login.response", response);
    }

    @KafkaListener(topics = "auth.validate", groupId = "authorization-group")
    public void consumeTokenValidationRequest(KafkaRequest request) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), Role.ROLE_ADMIN);

            final ObjectMapper objectMapper = new ObjectMapper();
            final String jwtTokenToValidate = objectMapper.convertValue(request.getPayload(), String.class);

            if (!jwtTokenUtil.validateToken(jwtTokenToValidate)) {
                response.setSuccess(false);
                response.setMessage("Invalid JWT token");
            } else {
                List<String> authorities = jwtTokenUtil.getAuthoritiesFromToken(jwtTokenToValidate);
                response.setSuccess(true);
                response.setMessage("Valid token");
                response.setData(authorities);
            }

            log.info("TOKEN VALIDATION request processed for requestId={}, success={}",
                    request.getRequestId(), response.isSuccess());
        } catch (Exception e) {
            log.error("Error processing TOKEN VALIDATION request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error validating token: " + e.getMessage());
        }

        kafkaTemplate.send("auth.validate.response", response);
    }

    @KafkaListener(topics = "user.create", groupId = "authorization-group")
    public void consumeCreateUserRequest(KafkaRequest request) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), Role.ROLE_ADMIN);

            final ObjectMapper objectMapper = new ObjectMapper();
            final UserDto userDto = objectMapper.convertValue(request.getPayload(), UserDto.class);

            final UserDto createdUser = userService.save(userDto);

            response.setData(createdUser);
            response.setSuccess(true);
            response.setMessage("User created successfully");

            log.info("CREATE_USER request processed successfully for profileId={}, userId={}",
                    userDto.getProfileId(), createdUser.getId());
        } catch (Exception e) {
            log.error("Error processing CREATE_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error creating user: " + e.getMessage());
            kafkaExceptionHandler.handleException(e, request.getRequestId());
        }

        kafkaTemplate.send("user.create.response", response);
    }

    @KafkaListener(topics = "user.update", groupId = "authorization-group")
    public void consumeUpdateUserRequest(KafkaRequest request) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), Role.ROLE_ADMIN);

            final ObjectMapper objectMapper = new ObjectMapper();
            final UserDto userDto = objectMapper.convertValue(request.getPayload(), UserDto.class);

            final UserDto updatedUser = userService.updateUser(userDto.getId(), userDto);
            response.setData(updatedUser);
            response.setSuccess(true);
            response.setMessage("User updated successfully");

            log.info("UPDATE_USER request processed successfully for userId={}", userDto.getId());
        } catch (Exception e) {
            log.error("Error processing UPDATE_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error updating user: " + e.getMessage());
            kafkaExceptionHandler.handleException(e, request.getRequestId());
        }

        kafkaTemplate.send("user.update.response", response);
    }

    @KafkaListener(topics = "user.delete", groupId = "authorization-group")
    public void consumeDeleteUserRequest(KafkaRequest request) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), Role.ROLE_ADMIN);

            final Long userId = Long.valueOf(request.getPayload().toString());

            userService.deleteById(userId);
            response.setSuccess(true);
            response.setMessage("User deleted successfully");

            log.info("DELETE_USER request processed successfully for userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing DELETE_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error deleting user: " + e.getMessage());
            kafkaExceptionHandler.handleException(e, request.getRequestId());
        }

        kafkaTemplate.send("user.delete.response", response);
    }

    @KafkaListener(topics = "user.get", groupId = "authorization-group")
    public void consumeGetUserRequest(KafkaRequest request) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), Role.ROLE_ADMIN);

            final Long userId = Long.valueOf(request.getPayload().toString());

            final UserDto user = userService.getUserById(userId);
            response.setData(user);
            response.setSuccess(true);
            response.setMessage("User fetched successfully");

            log.info("GET_USER request processed successfully for userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing GET_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error fetching user: " + e.getMessage());
            kafkaExceptionHandler.handleException(e, request.getRequestId());
        }

        kafkaTemplate.send("user.get.response", response);
    }

    @KafkaListener(topics = "user.get.all", groupId = "authorization-group")
    public void consumeGetAllUsersRequest(KafkaRequest request) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), Role.ROLE_ADMIN);

            response.setData(userService.getAllUsers());
            response.setSuccess(true);
            response.setMessage("Users fetched successfully");

            log.info("GET_ALL_USERS request processed successfully");
        } catch (Exception e) {
            log.error("Error processing GET_ALL_USERS request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error fetching users: " + e.getMessage());
            kafkaExceptionHandler.handleException(e, request.getRequestId());
        }

        kafkaTemplate.send("user.get.all.response", response);
    }

    private void validateTokenAndCheckPermissions(String jwtToken, Role requiredRole) {
        if (!jwtTokenUtil.validateToken(jwtToken)) {
            throw new SecurityException("Invalid JWT token");
        }

        final List<String> authorities = jwtTokenUtil.getAuthoritiesFromToken(jwtToken);

        if (!authorities.contains(requiredRole.name())) {
            throw new SecurityException("User does not have permission to perform this operation");
        }
    }
}
