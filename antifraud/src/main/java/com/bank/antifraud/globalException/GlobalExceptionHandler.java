package com.bank.antifraud.globalException;

import com.bank.antifraud.globalException.errorDto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;


@Slf4j
@ControllerAdvice
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
        } else if (exception instanceof DataAccessException) {
            errorResponse = new ErrorResponse("DATA_ACCESS_ERROR", exception.getMessage());
            log.error("Data access error: {}", exception.getMessage());
        } else if (exception instanceof IllegalArgumentException) {
            errorResponse = new ErrorResponse("ILLEGAL_ARGUMENT", exception.getMessage());
            log.error("Illegal argument: {}", exception.getMessage());
        } else if (exception instanceof JsonProcessingException) {
            errorResponse = new ErrorResponse("JSON_PARSING_ERROR", exception.getMessage());
            log.error("Json parsing error: {}", exception.getMessage());
        } else if (exception instanceof SecurityException) {
            errorResponse = new ErrorResponse("SECURITY_EXCEPTION", exception.getMessage());
            log.error("JWT is invalid: {}", exception.getMessage());
        }  else {
            errorResponse = new ErrorResponse("INTERNAL_ERROR", exception.getMessage());
            log.error("Unexpected error: {}", exception.getMessage());
        }

        kafkaTemplate.send(topic, errorResponse);
    }
}

