package com.bank.account.consumers;

import com.bank.account.ENUM.OperationType;
import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import com.bank.account.producers.AuditProducer;
import com.bank.account.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditConsumer {

    @Value("${spring.kafka.consumer.group-ids.audit}")
    private String auditGroup;

    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final AuditProducer auditProducer;

    @KafkaListener(topics = "${kafka.topics.audit-logs}", groupId = "#{__auditGroup}")
    public void handleAuditLogEvent(ConsumerRecord<String, String> record) {
        try {
            final String operationTypeHeader = new String(
                    record.headers().lastHeader("operationType").value(),
                    StandardCharsets.UTF_8);
            OperationType operationType = OperationType.valueOf(operationTypeHeader.toUpperCase());
            final AccountDto accountDto = objectMapper.readValue(record.value(), AccountDto.class);

            switch (operationType) {
                case CREATE -> {
                    final AuditDto auditDto = auditService.createAudit(accountDto);
                    auditProducer.sendAuditLogEvent(auditDto);
                    log.info("Create operation processed successfully");
                }
                case UPDATE -> {
                    final AuditDto auditDto = auditService.updateAudit(accountDto);
                    auditProducer.sendAuditLogEvent(auditDto);
                    log.info("Update operation processed successfully");
                }
                default -> {
                    log.error("Unknown operation type!");
                    auditProducer.sendAuditLogEvent();
                }
            }
} catch (Exception e) {
            log.error("Failed to process audit log event: ", e);
        }
    }
}
