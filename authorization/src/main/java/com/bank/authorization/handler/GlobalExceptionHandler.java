package com.bank.authorization.handler;

import com.bank.authorization.dto.KafkaResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalExceptionHandler {

    private final KafkaTemplate<String, KafkaResponse> kafkaTemplate;

    public GlobalExceptionHandler(KafkaTemplate<String, KafkaResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "error.handler", groupId = "authorization-group")
    public void handleException(ConsumerRecord<String, String> record) {
        String exceptionMessage = record.value();
        KafkaResponse response = new KafkaResponse();
        response.setRequestId(record.key());
        response.setSuccess(false);

        if (exceptionMessage.contains("EntityNotFoundException")) {
            response.setMessage("Entity not found: " + exceptionMessage);
        } else if (exceptionMessage.contains("ValidationException") || exceptionMessage.contains("ConstraintViolationException")) {
            response.setMessage("Validation error: " + exceptionMessage);
        } else if (exceptionMessage.contains("IllegalArgumentException")) {
            response.setMessage("Invalid argument: " + exceptionMessage);
        } else {
            response.setMessage("Internal server error: " + exceptionMessage);
        }

        log.error("Handled exception: {}", exceptionMessage);
        kafkaTemplate.send("error.responses", response);
    }
}
