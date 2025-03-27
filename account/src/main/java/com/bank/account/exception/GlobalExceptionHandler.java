package com.bank.account.exception;

import com.bank.account.exception.error_dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void handleException(Exception exception, String topic) {
        final ErrorResponse errorResponse;

        if (exception instanceof EntityNotFoundException) {
            errorResponse = new ErrorResponse("ENTITY_NOT_FOUND", exception.getMessage(), null);
            log.error("Entity not found: {}", exception.getMessage());
        } else if (exception instanceof ValidationException) {
            errorResponse = new ErrorResponse("VALIDATION_ERROR", exception.getMessage(), null);
            log.error("Validation error: {}", exception.getMessage());
        } else if (exception instanceof DataAccessException) {
            errorResponse = new ErrorResponse("DATA_ACCESS_ERROR", exception.getMessage(), null);
            log.error("Data access error: {}", exception.getMessage());
        } else if (exception instanceof IllegalArgumentException) {
            errorResponse = new ErrorResponse("ILLEGAL_ARGUMENT", exception.getMessage(), null);
            log.error("Illegal argument: {}", exception.getMessage());
        } else if (exception instanceof JsonProcessingException) {
            errorResponse = new ErrorResponse("JSON_PARSING_ERROR", exception.getMessage(), null);
            log.error("Json parsing error: {}", exception.getMessage());
        } else if (exception instanceof SecurityException) {
            errorResponse = new ErrorResponse("SECURITY_EXCEPTION", exception.getMessage(), null);
            log.error("JWT is invalid: {}", exception.getMessage());
        }  else {
            errorResponse = new ErrorResponse("INTERNAL_ERROR", exception.getMessage(), null);
            log.error("Unexpected error: {}", exception.getMessage());
        }

        kafkaTemplate.send(topic, errorResponse);
    }
}
