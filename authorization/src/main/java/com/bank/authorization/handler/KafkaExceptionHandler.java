package com.bank.authorization.handler;

import com.bank.authorization.dto.KafkaResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaExceptionHandler {

    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;

    public void handleException(Exception exception, String requestId) {
        final KafkaResponse response = new KafkaResponse();
        response.setRequestId(requestId);
        response.setSuccess(false);
        response.setMessage(getErrorMessage(exception));

        log.error("Kafka exception caught: {}", exception.getMessage(), exception);

        kafkaTemplate.send("error.logging", response);
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof ValidationException) {
            return "Validation failed: " + exception.getMessage();
        } else if (exception instanceof EntityNotFoundException) {
            return "Entity not found: " + exception.getMessage();
        } else if (exception instanceof BadCredentialsException) {
            return "Invalid credentials";
        } else if (exception instanceof SecurityException) {
            return "Security violation: " + exception.getMessage();
        } else if (exception instanceof DataAccessException) {
            return "Database error: " + exception.getMessage();
        } else {
            return "Unexpected error: " + exception.getMessage();
        }
    }
}
