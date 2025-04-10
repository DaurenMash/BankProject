package com.bank.antifraud.kafkaProducer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SuspiciousTransferProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private SuspiciousTransferProducer suspiciousTransferProducer;

    @Test
    void createEvent_ShouldSendToCreateTopic() {
        // Arrange
        Map<String, Object> transferData = Map.of("id", 1, "status", "suspicious");

        // Act
        suspiciousTransferProducer.createEvent(transferData);

        // Assert
        verify(kafkaTemplate).send(eq("suspicious-transfers.create"), any());
    }

    @Test
    void updateEvent_ShouldSendToUpdateTopic() {
        // Arrange
        Map<String, Object> transferData = Map.of("id", 1, "status", "updated");

        // Act
        suspiciousTransferProducer.updateEvent(transferData);

        // Assert
        verify(kafkaTemplate).send(eq("suspicious-transfers.update"), any());
    }

    @Test
    void deleteEvent_ShouldSendToDeleteTopic() {
        // Arrange
        Map<String, Object> transferData = Map.of("id", 1, "action", "delete");

        // Act
        suspiciousTransferProducer.deleteEvent(transferData);

        // Assert
        verify(kafkaTemplate).send(eq("suspicious-transfers.delete"), any());
    }

    @Test
    void getEvent_ShouldSendToGetTopic() {
        // Arrange
        Map<String, Object> transferData = Map.of("id", 1, "action", "get");

        // Act
        suspiciousTransferProducer.getEvent(transferData);

        // Assert
        verify(kafkaTemplate).send(eq("suspicious-transfers.get"), any());
    }

    @Test
    void eventResponse_ShouldSendToResponseTopic() {
        // Arrange
        Map<String, Object> transferData = Map.of("id", 1, "response", "success");

        // Act
        suspiciousTransferProducer.eventResponse(transferData);

        // Assert
        verify(kafkaTemplate).send(eq("suspicious-transfers.Response"), any());
    }

    @Test
    void allEvents_ShouldSendCorrectData() {
        // Arrange
        Map<String, Object> testData = Map.of(
                "transactionId", "12345",
                "amount", 10000,
                "reason", "suspicious activity"
        );

        // Act & Assert for each method
        suspiciousTransferProducer.createEvent(testData);
        verify(kafkaTemplate).send(eq("suspicious-transfers.create"), eq(testData));

        suspiciousTransferProducer.updateEvent(testData);
        verify(kafkaTemplate).send(eq("suspicious-transfers.update"), eq(testData));

        suspiciousTransferProducer.deleteEvent(testData);
        verify(kafkaTemplate).send(eq("suspicious-transfers.delete"), eq(testData));

        suspiciousTransferProducer.getEvent(testData);
        verify(kafkaTemplate).send(eq("suspicious-transfers.get"), eq(testData));

        suspiciousTransferProducer.eventResponse(testData);
        verify(kafkaTemplate).send(eq("suspicious-transfers.Response"), eq(testData));
    }
}
