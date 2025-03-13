package com.bank.authorization.service;

import com.bank.authorization.dto.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KafkaConsumerService {

    private final UserService userService;
    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;
    private final JwtTokenProvider jwtTokenProvider; // Сервис для работы с JWT
    private final AuthenticationManager authenticationManager;

    public KafkaConsumerService(UserService userService,
                                KafkaTemplate<String, KafkaResponse> kafkaTemplate,
                                JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }
    @KafkaListener(topics = "auth.login", groupId = "authorization-group")
    public void handleLoginRequest(AuthRequest authRequest) {
        log.info("Received LOGIN request for user: {}", authRequest.getUsername());

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(authRequest.getUsername());

        try {
            // Аутентификация пользователя
            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Генерация JWT-токена
            final String jwt = jwtTokenProvider.generateToken(authRequest.getUsername(), authenticate.getAuthorities());

            // Формирование ответа
            final AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setAuthorities(authenticate.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            response.setData(authResponse);
            response.setSuccess(true);
            response.setMessage("Login successful");
            log.info("LOGIN request processed successfully for user: {}", authRequest.getUsername());
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", authRequest.getUsername(), e);
            response.setSuccess(false);
            response.setMessage("Invalid username or password");
        } catch (Exception e) {
            log.error("Error processing LOGIN request: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error processing login request");
        }

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("auth.login.response", response);
    }
    // Обработка создания пользователя
    @KafkaListener(topics = "user.create", groupId = "authorization-group")
    public void handleCreateUser(KafkaRequest request) {
        log.info("Received CREATE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getJwtToken()); // Используем JWT-токен как requestId

        try {
            // Проверка JWT-токена и прав доступа
            validateTokenAndCheckPermissions(request.getJwtToken(), "ADMIN");
            // Извлечение данных из payload
            UserDto userDto = (UserDto) request.getPayload();

            // Создание пользователя
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

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.create.response", response);
    }

    // Обработка обновления пользователя
    @KafkaListener(topics = "user.update", groupId = "authorization-group")
    public void handleUpdateUser(KafkaRequest request) {
        log.info("Received UPDATE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getPayload().toString());

        try {
            // Проверка JWT-токена и прав доступа
            validateTokenAndCheckPermissions(request.getJwtToken(), "ADMIN");
            // Извлечение данных из payload
            UserDto userDto = (UserDto) request.getPayload();

            // Обновление пользователя
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

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.update.response", response);
    }

    // Обработка удаления пользователя
    @KafkaListener(topics = "user.delete", groupId = "authorization-group")
    public void handleDeleteUser(KafkaRequest request) {
        log.info("Received DELETE_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getPayload().toString());

        try {
            // Проверка JWT-токена и прав доступа
            validateTokenAndCheckPermissions(request.getJwtToken(), "ADMIN");
            // Извлечение данных из payload
            Long userId = Long.valueOf(request.getPayload().toString());

            // Удаление пользователя
            userService.deleteById(userId);
            response.setSuccess(true);
            response.setMessage("User deleted successfully");
            log.info("DELETE_USER request processed successfully for userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing DELETE_USER request: error={}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Error deleting user: " + e.getMessage());
        }

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.delete.response", response);
    }

    // Обработка получения пользователя по ID
    @KafkaListener(topics = "user.get", groupId = "authorization-group")
    public void handleGetUser(KafkaRequest request) {
        log.info("Received GET_USER request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(request.getPayload().toString());

        try {
            // Проверка JWT-токена и прав доступа
            validateTokenAndCheckPermissions(request.getJwtToken(), "ADMIN");
            // Извлечение данных из payload
            Long userId = Long.valueOf(request.getPayload().toString());

            // Получение пользователя
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

        // Отправка ответа в топик для ответов
        kafkaTemplate.send("user.get.response", response);
    }

    // Обработка получения всех пользователей
    @KafkaListener(topics = "user.get.all", groupId = "authorization-group")
    public void handleGetAllUsers(KafkaRequest request) {
        log.info("Received GET_ALL_USERS request");

        final KafkaResponse response = new KafkaResponse();
        response.setRequestId("ALL_USERS"); // Уникальный идентификатор для запроса всех пользователей

        try {
            // Проверка JWT-токена и прав доступа
            validateTokenAndCheckPermissions(request.getJwtToken(), "ADMIN");

            // Получение всех пользователей
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
    private void validateTokenAndCheckPermissions(String jwtToken, String requiredRole) {
        // Проверка JWT-токена
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new SecurityException("Invalid JWT token");
        }

        // Извлечение ролей из токена
        List<String> authorities = jwtTokenProvider.getAuthoritiesFromToken(jwtToken);

        // Проверка прав доступа
        if (!authorities.contains(new SimpleGrantedAuthority(requiredRole))) {
            throw new SecurityException("User does not have permission to perform this operation");
        }
    }
}