package com.bank.account.producers;

import com.bank.account.enums.OperationType;
import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditProducer {

    @Value("${kafka.topics.audit-logs}")
    private String auditLogTopic;

    @Value("${kafka.topics.external-audit-logs}")
    private String externalAuditLogTopic;

    private final KafkaTemplate<String, AuditDto> kafkaTemplateAudit;
    private final KafkaTemplate<String, AccountDto> kafkaTemplateAccount;

    public void sendAuditLogEvent(AuditDto auditDto) {
        try {
            kafkaTemplateAudit.send(externalAuditLogTopic, auditDto);
            log.info("Audit log sent to Kafka successfully");
        } catch (Exception e) {
            log.error("Failed to send audit log: ", e);
            throw new RuntimeException("Failed to send audit log", e);
        }
    }

    public void sendAuditLogRequest(AccountDto result, OperationType operationType) {
        try {
            final ProducerRecord<String, AccountDto> record = new ProducerRecord<>(auditLogTopic, result);
            record.headers()
                    .add(new RecordHeader("operationType", operationType.name().getBytes(StandardCharsets.UTF_8)));

            kafkaTemplateAccount.send(record);
            log.info("Message sent to Kafka with operationType header: {}", operationType);
        } catch (Exception e) {
            log.error("Failed to send message to Kafka: ", e);
        }
    }
}
