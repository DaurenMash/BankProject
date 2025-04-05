package com.bank.publicinfo.exception;

import com.bank.publicinfo.dto.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final KafkaTemplate<String, ErrorResponseDto> kafkaTemplate;

    public void handleException(Exception exc, String topic) {
        final ErrorResponseDto errorResponse;

        if (exc instanceof EntityNotFoundException) {
            errorResponse = new ErrorResponseDto("ENTITY_NOT_FOUND", exc.getMessage());
            log.error("Entity not found: {}", exc.getMessage());
        } else if (exc instanceof ValidationException) {
            errorResponse = new ErrorResponseDto("VALIDATION_ERROR", exc.getMessage());
            log.error("Validation error: {}", exc.getMessage());
        } else if (exc instanceof DataAccessException) {
            errorResponse = new ErrorResponseDto("DATA_ACCESS_ERROR", exc.getMessage());
            log.error("Data access error: {}", exc.getMessage());
        } else if (exc instanceof IllegalArgumentException) {
            errorResponse = new ErrorResponseDto("ILLEGAL_ARGUMENT", exc.getMessage());
            log.error("Illegal argument: {}", exc.getMessage());
        } else if (exc instanceof CustomJsonProcessingException) {
            errorResponse = new ErrorResponseDto("JSON_PARSING_ERROR", exc.getMessage());
            log.error("Json parsing error: {}", exc.getMessage());
        } else if (exc instanceof SecurityException) {
            errorResponse = new ErrorResponseDto("SECURITY_EXCEPTION", exc.getMessage());
            log.error("JWT is invalid: {}", exc.getMessage());
        }  else {
            errorResponse = new ErrorResponseDto("INTERNAL_ERROR", exc.getMessage());
            log.error("Unexpected error: {}", exc.getMessage());
        }

        kafkaTemplate.send(topic, errorResponse);
    }

}
