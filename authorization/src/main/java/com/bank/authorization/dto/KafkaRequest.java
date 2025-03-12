package com.bank.authorization.dto;

import lombok.Data;

@Data
public class KafkaRequest {
    private String requestId; // Уникальный идентификатор запроса
    private String operation; // Тип операции (например, "GET_ALL_USERS", "GET_USER_BY_ID", "CREATE_USER")
    private Object payload; // Данные для выполнения операции (например, ID пользователя или DTO пользователя)
}
