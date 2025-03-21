package com.bank.account.producers;

import com.bank.account.dto.AuditDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

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

    public void sentAuditLogRequest(Object result, String operationType) {
        try {
            final ProducerRecord<String, Object> record = new ProducerRecord<>("audit.logs", null, result);
            record.headers().add(new RecordHeader("operationType", operationType.getBytes(StandardCharsets.UTF_8)));

            kafkaTemplate.send(record);
            log.info("Message sent to Kafka with operationType header: {}", operationType);
        } catch (Exception e) {
            log.error("Failed to send message to Kafka: {}", e.getMessage());
        }
    }
}
