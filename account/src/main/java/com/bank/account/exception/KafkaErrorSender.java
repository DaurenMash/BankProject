package com.bank.account.exception;

import com.bank.account.exception.error_dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaErrorSender {
    private final KafkaTemplate<String, ErrorResponse> kafkaTemplate;
    private final GlobalExceptionHandler globalExceptionHandler;

    public void sendError(Exception e, String topic) {
        try {
            final ErrorResponse errorResponse = globalExceptionHandler.handleException(e);
            kafkaTemplate.send(topic, errorResponse);
            log.error("Error sent to Kafka topic '{}': {}", topic, errorResponse, e);
        } catch (Exception ex) {
            log.error("Failed to send error to Kafka", ex);
        }
    }
}
