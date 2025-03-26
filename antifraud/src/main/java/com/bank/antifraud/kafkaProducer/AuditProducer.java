package com.bank.antifraud.kafkaProducer;

import com.bank.antifraud.dto.AuditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void sendAuditLog(AuditDto auditDto) {
        try {
            kafkaTemplate.send("audit-topic", auditDto);
            log.info("Audit log sent to Kafka successfully");
        } catch (Exception e) {
            log.error("Failed to send audit log {}", e.getMessage());
        }
    }

    public void sentAuditLogRequest(Object result, String operationType) {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>("audit.logs", null, result);
            record.headers().add(new RecordHeader("operationType", operationType.getBytes(StandardCharsets.UTF_8)));

            kafkaTemplate.send(record);
            log.info("Message sent to Kafka with operationType header: {}", operationType);
        } catch (Exception e) {
            log.error("Failed to send message to Kafka: {}", e.getMessage());
        }
    }

}
