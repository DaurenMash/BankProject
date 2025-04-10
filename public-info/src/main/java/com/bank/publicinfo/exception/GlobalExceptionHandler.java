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
        final ErrorResponseDto errorResponse = buildErrorResponse(exc);
        log.error("{}: ", errorResponse.getErrorCode(), exc);
        kafkaTemplate.send(topic, errorResponse);
    }

    private ErrorResponseDto buildErrorResponse(Exception exc) {
        if (exc instanceof EntityNotFoundException) {
            return new ErrorResponseDto("ENTITY_NOT_FOUND", exc.toString());
        } else if (exc instanceof ValidationException) {
            return new ErrorResponseDto("VALIDATION_ERROR", exc.toString());
        } else if (exc instanceof DataAccessException) {
            return new ErrorResponseDto("DATA_ACCESS_ERROR", exc.toString());
        } else if (exc instanceof IllegalArgumentException) {
            return new ErrorResponseDto("ILLEGAL_ARGUMENT", exc.toString());
        } else if (exc instanceof CustomJsonProcessingException) {
            return new ErrorResponseDto("JSON_PARSING_ERROR", exc.toString());
        } else if (exc instanceof SecurityException) {
            return new ErrorResponseDto("SECURITY_EXCEPTION", exc.toString());
        } else {
            return new ErrorResponseDto("INTERNAL_ERROR", exc.toString());
        }
    }
}
