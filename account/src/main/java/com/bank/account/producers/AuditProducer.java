package com.bank.account.producers;

import com.bank.account.ENUM.OperationType;
import com.bank.account.dto.AuditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditProducer {
    private final KafkaTemplate<String, AuditDto> kafkaTemplate;

    public void sendAuditLogEvent(AuditDto auditDto) {
        try {
            kafkaTemplate.send("external.audit.logs", auditDto);
            log.info("Audit log sent to Kafka successfully");
        } catch (Exception e) {
            log.error("Failed to send audit log: ", e);
        }
    }

    public void sendAuditLogRequest(Object result, OperationType operationType) {
        try {
            final ProducerRecord<String, AuditDto> record = new ProducerRecord<>("audit.logs", null, result);
            record.headers().add(new RecordHeader("operationType", operationType.name().getBytes(StandardCharsets.UTF_8)));

            kafkaTemplate.send(record);
            log.info("Message sent to Kafka with operationType header: {}", operationType);
        } catch (Exception e) {
            log.error("Failed to send message to Kafka: ", e);
        }
    }
}
