package com.bank.antifraud.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void creatEvent(Map<String, Object> transferData) {
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

    public void EventResponse(Map<String, Object> transferData) {
        kafkaTemplate.send("suspicious-transfers.createResponse", transferData);
    }
}
