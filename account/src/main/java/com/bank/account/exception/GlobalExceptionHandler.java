package com.bank.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;


@Slf4j
@Component
public class GlobalExceptionHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public GlobalExceptionHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handleException(Exception exception, String topic) {
        ErrorResponse errorResponse;

        if (exception instanceof EntityNotFoundException) {
            errorResponse = new ErrorResponse("ENTITY_NOT_FOUND", exception.getMessage());
            log.error("Entity not found: {}", exception.getMessage());
        } else if (exception instanceof ValidationException) {
            errorResponse = new ErrorResponse("VALIDATION_ERROR", exception.getMessage());
            log.error("Validation error: {}", exception.getMessage());
        } else {
            errorResponse = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
            log.error("Unexpected error: {}", exception.getMessage());
        }

        kafkaTemplate.send(topic, errorResponse);
    }
}
