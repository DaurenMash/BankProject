package com.bank.antifraud.kafkaProducer;

import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Service
public class SuspiciousTransferProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SuspiciousTransferProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void createEvent(Map<String, Object> transferData) {
        kafkaTemplate.send("suspicious-transfers.create", transferData);
    }

    public void updateEvent(Map<String, Object> transferData) {
        kafkaTemplate.send("suspicious-transfers.update", transferData);
    }

    public void deleteEvent(Map<String, Object> transferData) {
        kafkaTemplate.send("suspicious-transfers.delete", transferData);
    }

    public void getEvent(Map<String, Object> transferData) {
        kafkaTemplate.send("suspicious-transfers.get", transferData);
    }

    public void eventResponse(Map<String, Object> transferData) {
        kafkaTemplate.send("suspicious-transfers.Response", transferData);
    }
}
