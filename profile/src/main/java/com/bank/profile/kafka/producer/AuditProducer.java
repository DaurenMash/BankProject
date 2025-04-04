package com.bank.profile.kafka.producer;

import com.bank.profile.config.KafkaTopicsConfig;
import com.bank.profile.dto.AuditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditProducer {
    private final KafkaTopicsConfig topicsConfig;
    private final KafkaTemplate<String, AuditDto> kafkaTemplateAudit;

    public void sendAudit(AuditDto dto) {
        kafkaTemplateAudit.send(topicsConfig.getTopicAudit(), dto);
    }
}
