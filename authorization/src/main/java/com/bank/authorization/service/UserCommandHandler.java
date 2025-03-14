package com.bank.authorization.service;

import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import com.bank.authorization.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandHandler {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public KafkaResponse handleCreateUser(KafkaRequest request) {
        log.info("Received CREATE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            validateTokenAndCheckPermissions(request.getJwtToken(), "ROLE_ADMIN");

            // Конвертируем payload в UserDto
            ObjectMapper objectMapper = new ObjectMapper();
            UserDto userDto = objectMapper.convertValue(request.getPayload(), UserDto.class);

            // Вызываем сервис для сохранения пользователя
            final UserDto createdUser = userService.save(userDto);

            response.setData(createdUser);
            response.setSuccess(true);
            response.setMessage("User created successfully");

            log.info("CREATE_USER request processed successfully for profileId={}", userDto.getProfileId());
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
        response.setRequestId(request.getRequestId()); // Передаем правильный requestId

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
