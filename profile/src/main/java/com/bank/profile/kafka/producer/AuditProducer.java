package com.bank.profile.kafka.producer;

import com.bank.profile.config.KafkaTopicsConfig;
import com.bank.profile.dto.AuditDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditProducer {
    private final KafkaTopicsConfig topicsConfig;
    private final KafkaTemplate<String, AuditDto> kafkaTemplateAudit;

    public AuditProducer(KafkaTopicsConfig topicsConfig, KafkaTemplate<String, AuditDto> kafkaTemplateAudit) {
        this.topicsConfig = topicsConfig;
        this.kafkaTemplateAudit = kafkaTemplateAudit;
    }

    public void sendAudit(AuditDto dto) {
        kafkaTemplateAudit.send(topicsConfig.getTopicAudit(), dto);
    }
}
