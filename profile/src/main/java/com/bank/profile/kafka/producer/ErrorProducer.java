package com.bank.profile.kafka.producer;

import com.bank.profile.config.KafkaTopicsConfig;
import com.bank.profile.dto.ErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorProducer {
    private final KafkaTopicsConfig topicsConfig;
    private final KafkaTemplate<String, ErrorDto> kafkaTemplate;

    public void sendError(ErrorDto dto) {
        kafkaTemplate.send(topicsConfig.getTopicError(), dto);
    }
}
