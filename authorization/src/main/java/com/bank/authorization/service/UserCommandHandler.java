package com.bank.authorization.service;

import com.bank.authorization.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UserCommandHandler {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public KafkaResponse handleLoginRequest(AuthRequest authRequest) {
        log.info("Received LOGIN request for user: {}", authRequest.getProfileId());

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(authRequest.getRequestId()); // Передаём requestId от клиента

        try {
            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getProfileId(), authRequest.getPassword())
            );

            final String jwt = jwtTokenProvider.generateToken(
                    String.valueOf(authRequest.getProfileId()),
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

        return response;
    }

    public KafkaResponse handleCreateUser(KafkaRequest request) {
        log.info("Received CREATE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), "ROLE_ADMIN");

            ObjectMapper objectMapper = new ObjectMapper();
            UserDto userDto = objectMapper.convertValue(request.getPayload(), UserDto.class);

            final UserDto createdUser = userService.save(userDto);

            response.setData(createdUser);
            response.setSuccess(true);
            response.setMessage("User created successfully");

            //response.setUserId(createdUser.getId());

            log.info("CREATE_USER request processed successfully for profileId={}, userId={}",
                    userDto.getProfileId(), createdUser.getId());
        } catch (Exception e) {
            log.error("Error processing CREATE_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error creating user: " + e.getMessage());
        }

        return response;
    }

    public KafkaResponse handleUpdateUser(KafkaRequest request) {
        log.info("Received UPDATE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), "ROLE_ADMIN");

            ObjectMapper objectMapper = new ObjectMapper();
            UserDto userDto = objectMapper.convertValue(request.getPayload(), UserDto.class);

            final UserDto updatedUser = userService.updateUser(userDto.getId(), userDto);
            response.setData(updatedUser);
            response.setSuccess(true);
            response.setMessage("User updated successfully");

            log.info("UPDATE_USER request processed successfully for userId={}", userDto.getId());
        } catch (Exception e) {
            log.error("Error processing UPDATE_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error updating user: " + e.getMessage());
        }

        return response;
    }

    public KafkaResponse handleDeleteUser(KafkaRequest request) {
        log.info("Received DELETE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

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

        return response;
    }

    public KafkaResponse handleGetUser(KafkaRequest request) {
        log.info("Received GET_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

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

        return response;
    }

    public KafkaResponse handleGetAllUsers(KafkaRequest request) {
        log.info("Received GET_ALL_USERS request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

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

        return response;
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
