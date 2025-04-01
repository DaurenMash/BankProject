package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.ATMDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ATMProducer {

    private final KafkaTemplate<String, ATMDto> kafkaTemplate;

    public void sendATM(String topic, ATMDto atmDto) {
        kafkaTemplate.send(topic, atmDto);
        log.info("ATM sent to topic {}", topic);
    }

}
