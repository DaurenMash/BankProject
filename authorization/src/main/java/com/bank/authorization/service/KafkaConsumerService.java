package com.bank.authorization.service;

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

    // Обработка создания пользователя
    @KafkaListener(topics = "user.create", groupId = "authorization-group")
    public void handleCreateUser(UserDto userDto) {
        log.info("Received CREATE_USER request for profileId={}", userDto.getProfileId());

        KafkaResponse response = new KafkaResponse();
        response.setRequestId(userDto.getProfileId().toString()); // Используем profileId как requestId

        try {
            UserDto createdUser = userService.save(userDto);
            response.setData(createdUser);
            response.setSuccess(true);
            response.setMessage("User created successfully");
            log.info("CREATE_USER request processed successfully for profileId={}", userDto.getProfileId());
        } catch (Exception e) {
            log.error("Error processing CREATE_USER request: profileId={}, error={}",
                    userDto.getProfileId(), e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error creating user: " + e.getMessage());
        }

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.create.response", response);
    }

    // Обработка обновления пользователя
    @KafkaListener(topics = "user.update", groupId = "authorization-group")
    public void handleUpdateUser(UserDto userDto) {
        log.info("Received UPDATE_USER request for userId={}", userDto.getId());

        KafkaResponse response = new KafkaResponse();
        response.setRequestId(userDto.getId().toString()); // Используем userId как requestId

        try {
            UserDto updatedUser = userService.updateUser(userDto.getId(), userDto);
            response.setData(updatedUser);
            response.setSuccess(true);
            response.setMessage("User updated successfully");
            log.info("UPDATE_USER request processed successfully for userId={}", userDto.getId());
        } catch (Exception e) {
            log.error("Error processing UPDATE_USER request: userId={}, error={}",
                    userDto.getId(), e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error updating user: " + e.getMessage());
        }

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.update.response", response);
    }

    // Обработка удаления пользователя
    @KafkaListener(topics = "user.delete", groupId = "authorization-group")
    public void handleDeleteUser(Long userId) {
        log.info("Received DELETE_USER request for userId={}", userId);

        KafkaResponse response = new KafkaResponse();
        response.setRequestId(userId.toString()); // Используем userId как requestId

        try {
            userService.deleteById(userId);
            response.setSuccess(true);
            response.setMessage("User deleted successfully");
            log.info("DELETE_USER request processed successfully for userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing DELETE_USER request: userId={}, error={}",
                    userId, e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error deleting user: " + e.getMessage());
        }

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.delete.response", response);
    }

    // Обработка получения пользователя по ID
    @KafkaListener(topics = "user.get", groupId = "authorization-group")
    public void handleGetUser(Long userId) {
        log.info("Received GET_USER request for userId={}", userId);

        KafkaResponse response = new KafkaResponse();
        response.setRequestId(userId.toString()); // Используем userId как requestId

        try {
            UserDto user = userService.getUserById(userId);
            response.setData(user);
            response.setSuccess(true);
            response.setMessage("User fetched successfully");
            log.info("GET_USER request processed successfully for userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing GET_USER request: userId={}, error={}",
                    userId, e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error fetching user: " + e.getMessage());
        }

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.get.response", response);
    }

    // Обработка получения всех пользователей
    @KafkaListener(topics = "user.get.all", groupId = "authorization-group")
    public void handleGetAllUsers() {
        log.info("Received GET_ALL_USERS request");

        KafkaResponse response = new KafkaResponse();
        response.setRequestId("ALL_USERS"); // Уникальный идентификатор для запроса всех пользователей

        try {
            response.setData(userService.getAllUsers());
            response.setSuccess(true);
            response.setMessage("Users fetched successfully");
            log.info("GET_ALL_USERS request processed successfully");
        } catch (Exception e) {
            log.error("Error processing GET_ALL_USERS request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error fetching users: " + e.getMessage());
        }

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.get.all.response", response);
    }
}