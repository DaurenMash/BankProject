package com.bank.authorization.handler;

import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.service.UserService;
import com.bank.authorization.utils.JwtValidator;
import com.bank.authorization.utils.ResponseFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandHandler {

    @Value("${topics.user_create_response}")
    private String userCreateResponseTopic;

    @Value("${topics.user_update_response}")
    private String userUpdateResponseTopic;

    @Value("${topics.user_delete_response}")
    private String userDeleteResponseTopic;

    @Value("${topics.user_get_response}")
    private String userGetResponseTopic;

    @Value("${topics.user_get_all_response}")
    private String userGetAllResponseTopic;


    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;
    private final ResponseFactory responseFactory;
    private final JwtValidator jwtValidator;

    public void handleCreateUser(KafkaRequest request) {
        KafkaResponse response;
        try {
            jwtValidator.validate(request.getJwtToken(), "ROLE_ADMIN");
            UserDto userDto = objectMapper.readValue(request.getPayload(), UserDto.class);
            UserDto createdUser = userService.save(userDto);
            response = responseFactory.createSuccessResponse(request.getRequestId(), "User created successfully", createdUser);
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON in payload: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Invalid JSON format in payload");
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Error creating user: " + e.getMessage());
        }
        kafkaTemplate.send(userCreateResponseTopic, response);
    }

    public void handleUpdateUser(KafkaRequest request) {
        KafkaResponse response;
        try {
            jwtValidator.validate(request.getJwtToken(), "ROLE_ADMIN");
            UserDto userDto = objectMapper.readValue(request.getPayload(), UserDto.class);
            UserDto updatedUser = userService.updateUser(userDto.getId(), userDto);
            response = responseFactory.createSuccessResponse(request.getRequestId(), "User updated successfully", updatedUser);
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON in payload: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Invalid JSON format in payload");
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Error updating user: " + e.getMessage());
        }
        kafkaTemplate.send(userUpdateResponseTopic, response);
    }

    public void handleDeleteUser(KafkaRequest request) {
        KafkaResponse response;
        try {
            jwtValidator.validate(request.getJwtToken(), "ROLE_ADMIN");
            Long userId = Long.valueOf(request.getPayload().toString());
            userService.deleteById(userId);
            response = responseFactory.createSuccessResponse(request.getRequestId(), "User deleted successfully", null);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Error deleting user: " + e.getMessage());
        }
        kafkaTemplate.send(userDeleteResponseTopic, response);
    }

    public void handleGetUser(KafkaRequest request) {
        KafkaResponse response;
        try {
            jwtValidator.validate(request.getJwtToken(), "ROLE_ADMIN");
            Long userId = Long.valueOf(request.getPayload().toString());
            UserDto user = userService.getUserById(userId);
            response = responseFactory.createSuccessResponse(request.getRequestId(), "User retrieved successfully", user);
        } catch (Exception e) {
            log.error("Error retrieving user: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Error retrieving user: " + e.getMessage());
        }
        kafkaTemplate.send(userGetResponseTopic, response);
    }

    public void handleGetAllUsers(KafkaRequest request) {
        KafkaResponse response;
        try {
            jwtValidator.validate(request.getJwtToken(), "ROLE_ADMIN");
            response = responseFactory.createSuccessResponse(request.getRequestId(), "Users retrieved successfully", userService.getAllUsers());
        } catch (Exception e) {
            log.error("Error retrieving all users: {}", e.getMessage());
            response = responseFactory.createErrorResponse(request.getRequestId(), "Error retrieving all users: " + e.getMessage());
        }
        kafkaTemplate.send(userGetAllResponseTopic, response);
    }
}
