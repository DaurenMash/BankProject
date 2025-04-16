package com.bank.transfer.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final KafkaTemplate<String, ErrorResponse> kafkaTemplate;

    public void handleException(Exception exception, String requestId) {
        final ErrorResponse response = getErrorResponse(exception, requestId);

        log.error("Exception caught: {}", exception.getMessage(), exception);

        kafkaTemplate.send("error.logging", response);
    }

    private ErrorResponse getErrorResponse(Exception exception, String requestId) {
        if (exception instanceof EntityNotFoundException) {
            return new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "The requested resource was not found: " + exception.getMessage(),
                    requestId
            );
        } else if (exception instanceof ValidationException) {
            return new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Error",
                    "Invalid input: " + exception.getMessage(),
                    requestId
            );
        } else if (exception instanceof IllegalArgumentException) {
            return new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Illegal Argument",
                    "Invalid argument: " + exception.getMessage(),
                    requestId
            );
        } else if (exception instanceof DataIntegrityViolationException) {
            return new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Data Integrity Violation",
                    "Database constraint violated: " + exception.getMessage(),
                    requestId
            );
        } else {
            return new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "Unexpected error: " + exception.getMessage(),
                    requestId
            );
        }
    }
}
