package com.bank.history.handlers;

import com.bank.history.dto.HistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HistoryKafkaProducer {

    private final KafkaTemplate<String, HistoryDto> kafkaTemplate;

    @Autowired
    public HistoryKafkaProducer(KafkaTemplate<String, HistoryDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, HistoryDto historyDto) {
        kafkaTemplate.send(topic, historyDto);
    }
}

