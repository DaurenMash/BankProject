package com.bank.account.consumers;

import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import com.bank.account.producers.AuditProducer;
import com.bank.account.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class AuditConsumer {
    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final AuditProducer auditProducer;

    public AuditConsumer(AuditService auditService, AuditProducer auditProducer,
                         ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "audit.logs", groupId = "audit-group")
    public void handleAuditLogEvent(ConsumerRecord<String, String> record) {
        try {
            String operationType = new String(
                    record.headers().lastHeader("operationType").value(),
                    StandardCharsets.UTF_8);
            AccountDto accountDto = objectMapper.readValue(record.value(), AccountDto.class);

            if ("create".equalsIgnoreCase(operationType)) {
                AuditDto auditDto = auditService.createAudit(accountDto);
                auditProducer.sendAuditLogEvent(auditDto);
                log.info("Create operation processed successfully");
            } else if ("update".equals(operationType)) {
                AuditDto auditDto = auditService.updateAudit(accountDto);
                auditProducer.sendAuditLogEvent(auditDto);
                log.info("Update operation processed successfully");
            } else {
                log.warn("Unknown operation type: {}", operationType);
            }
        } catch (Exception e) {
            log.error("Failed to process audit log event: {}", e.getMessage());
        }
    }
}
