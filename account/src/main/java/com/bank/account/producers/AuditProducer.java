package com.bank.account.producers;

import com.bank.account.dto.AuditDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuditProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuditLogEvent(AuditDto auditDto) {
        try {
            kafkaTemplate.send("external.audit.logs", auditDto);
            log.info("Audit log sent to Kafka successfully");
        } catch (Exception e) {
            log.error("Failed to send audit log {}", e.getMessage());
        }
    }
}
