package com.bank.account.consumers;

import com.bank.account.ENUM.OperationType;
import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import com.bank.account.exception.KafkaErrorSender;
import com.bank.account.producers.AuditProducer;
import com.bank.account.service.AuditService;
import com.bank.account.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditConsumer {

    @Value("${spring.kafka.consumer.group-ids.audit}")
    private String auditGroup;

    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final AuditProducer auditProducer;
    private final KafkaErrorSender kafkaErrorSender;

    @KafkaListener(topics = "${kafka.topics.audit-logs}", groupId = "@auditConsumer.auditGroup",
            containerFactory = "auditKafkaListenerContainerFactory")
    public void handleAuditLogEvent(ConsumerRecord<String, AccountDto> record) {
        try {
            final String operationTypeHeader = JsonUtils.extractHeader(record, "operationType");
            if (operationTypeHeader == null) {
                throw new IllegalArgumentException("Missing required header 'operationType'");
            }

            final OperationType operationType = OperationType.valueOf(operationTypeHeader.toUpperCase());
            final AccountDto accountDto = record.value();

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
                    final String errorMsg = String.format("Unsupported operation type: '%s'", operationType);
                    log.error(errorMsg);
                    kafkaErrorSender.sendError(new UnsupportedOperationException(errorMsg), record.value().toString());
                }
            }
        } catch (Exception e) {
            log.error("Failed to process audit log event for record: {}", record, e);
            kafkaErrorSender.sendError(e, record.value().toString());
        }
    }
}
