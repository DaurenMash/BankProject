package com.bank.authorization.service;

import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import com.bank.authorization.dto.UserDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaConsumerService {

    private final UserService userService;
    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;

    public KafkaConsumerService(UserService userService, KafkaTemplate<String, KafkaResponse> kafkaTemplate) {
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "authorization-requests", groupId = "authorization-group")
    public void handleRequest(KafkaRequest request) {
        log.info("Received Kafka request: requestId={}, operation={}", request.getRequestId(), request.getOperation());

        KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getRequestId());

        try {
            switch (request.getOperation()) {
                case "GET_ALL_USERS":
                    log.debug("Processing GET_ALL_USERS request");
                    response.setData(userService.getAllUsers());
                    response.setSuccess(true);
                    response.setMessage("Users fetched successfully");
                    log.info("GET_ALL_USERS request processed successfully");
                    break;

                case "GET_USER_BY_ID":
                    Long userId = Long.valueOf(request.getPayload().toString());
                    log.debug("Processing GET_USER_BY_ID request for userId={}", userId);
                    response.setData(userService.getUserById(userId));
                    response.setSuccess(true);
                    response.setMessage("User fetched successfully");
                    log.info("GET_USER_BY_ID request processed successfully for userId={}", userId);
                    break;

                case "CREATE_USER":
                    UserDto userDto = (UserDto) request.getPayload();
                    log.debug("Processing CREATE_USER request for profileId={}", userDto.getProfileId());
                    response.setData(userService.save(userDto));
                    response.setSuccess(true);
                    response.setMessage("User created successfully");
                    log.info("CREATE_USER request processed successfully for profileId={}", userDto.getProfileId());
                    break;

                case "UPDATE_USER":
                    UserDto updateUserDto = (UserDto) request.getPayload();
                    log.debug("Processing UPDATE_USER request for userId={}", updateUserDto.getId());
                    response.setData(userService.updateUser(updateUserDto.getId(), updateUserDto));
                    response.setSuccess(true);
                    response.setMessage("User updated successfully");
                    log.info("UPDATE_USER request processed successfully for userId={}", updateUserDto.getId());
                    break;

                case "DELETE_USER":
                    Long deleteUserId = Long.valueOf(request.getPayload().toString());
                    log.debug("Processing DELETE_USER request for userId={}", deleteUserId);
                    userService.deleteById(deleteUserId);
                    response.setSuccess(true);
                    response.setMessage("User deleted successfully");
                    log.info("DELETE_USER request processed successfully for userId={}", deleteUserId);
                    break;

                default:
                    log.warn("Unsupported operation: {}", request.getOperation());
                    response.setSuccess(false);
                    response.setMessage("Unsupported operation: " + request.getOperation());
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing request: requestId={}, operation={}, error={}",
                    request.getRequestId(), request.getOperation(), e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error processing request: " + e.getMessage());
        }

        // Отправка ответа в топик для ответов
        log.debug("Sending Kafka response: requestId={}, success={}, message={}",
                response.getRequestId(), response.isSuccess(), response.getMessage());
        kafkaTemplate.send("authorization-responses", response);
    }
}