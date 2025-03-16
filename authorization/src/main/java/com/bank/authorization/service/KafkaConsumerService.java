package com.bank.authorization.service;

import com.bank.authorization.dto.AuthRequest;
import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для обработки входящих Kafka-сообщений и отправки ответов.
 * Использует KafkaTemplate для отправки ответов на соответствующие топики.
 * Пришлось сделать вот такой класс, так как если пометить аннотацией
 * @KafkaListener методы прямо в классе UserCommandHandler, то так не
 * работает AOP из-за того, что и AOP и @KafkaListener обе работают через
 * прокси и получается, что @KafkaListener работает, AOP нет.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;
    private final UserCommandHandler userCommandHandler;

    /**
     * Обрабатывает запрос на аутентификацию пользователя.
     *
     * @param request объект запроса с учетными данными пользователя.
     */
    @KafkaListener(topics = "auth.login", groupId = "authorization-group")
    public void consumeLoginRequest(AuthRequest request) {
        KafkaResponse response = userCommandHandler.handleLoginRequest(request);
        kafkaTemplate.send("auth.login.response", response);
    }

    /**
     * Обрабатывает запрос на создание пользователя.
     *
     * @param request объект запроса с данными нового пользователя.
     */
    @KafkaListener(topics = "user.create", groupId = "authorization-group")
    public void consumeCreateUserRequest(KafkaRequest request) {
        KafkaResponse response = userCommandHandler.handleCreateUser(request);
        kafkaTemplate.send("user.create.response", response);
    }

    /**
     * Обрабатывает запрос на обновление данных пользователя.
     *
     * @param request объект запроса с обновленными данными пользователя.
     */
    @KafkaListener(topics = "user.update", groupId = "authorization-group")
    public void consumeUpdateUserRequest(KafkaRequest request) {
        KafkaResponse response = userCommandHandler.handleUpdateUser(request);
        kafkaTemplate.send("user.update.response", response);
    }

    /**
     * Обрабатывает запрос на удаление пользователя.
     *
     * @param request объект запроса с идентификатором пользователя для удаления.
     */
    @KafkaListener(topics = "user.delete", groupId = "authorization-group")
    public void consumeDeleteUserRequest(KafkaRequest request) {
        KafkaResponse response = userCommandHandler.handleDeleteUser(request);
        kafkaTemplate.send("user.delete.response", response);
    }

    /**
     * Обрабатывает запрос на получение информации о пользователе.
     *
     * @param request объект запроса с идентификатором запрашиваемого пользователя.
     */
    @KafkaListener(topics = "user.get", groupId = "authorization-group")
    public void consumeGetUserRequest(KafkaRequest request) {
        KafkaResponse response = userCommandHandler.handleGetUser(request);
        kafkaTemplate.send("user.get.response", response);
    }

    /**
     * Обрабатывает запрос на получение списка всех пользователей.
     *
     * @param request здесь только JWT-токен передаётся для проверки прав пользователя.
     */
    @KafkaListener(topics = "user.get.all", groupId = "authorization-group")
    public void consumeGetAllUsersRequest(KafkaRequest request) {
        KafkaResponse response = userCommandHandler.handleGetAllUsers(request);
        kafkaTemplate.send("user.get.all.response", response);
    }
}
