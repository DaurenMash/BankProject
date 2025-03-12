package com.bank.authorization.dto;

import lombok.Data;

@Data
public class KafkaResponse {
    private String requestId; // Уникальный идентификатор запроса
    private boolean success; // Успех или ошибка
    private String message; // Сообщение об ошибке или успехе
    private Object data; // Данные результата (например, список пользователей или DTO пользователя)
}
