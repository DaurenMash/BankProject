package com.bank.history.handlers;

import com.bank.history.dto.HistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryKafkaProducer {

    private final KafkaTemplate<String, HistoryDto> kafkaTemplate;


    public void sendMessage(String topic, HistoryDto historyDto) {
        kafkaTemplate.send(topic, historyDto);
    }
}

