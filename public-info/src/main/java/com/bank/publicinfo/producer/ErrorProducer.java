package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorProducer {

    private final KafkaTemplate<String, ErrorResponseDto> kafkaTemplate;

    public void sendErrorLog(String topic, ErrorResponseDto errorResponseDto) {
        kafkaTemplate.send(topic, errorResponseDto);
        log.info("Error or exception response sent to topic: {}", topic);
    }

}
