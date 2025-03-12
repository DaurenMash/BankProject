package com.bank.account.consumers;

import com.bank.account.dto.AuditDto;
import com.bank.account.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuditService auditService;

    public AuditConsumer(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(topics = "audit.logs", groupId = "audit-group")
    public void handleAuditLogEvent(String message) {
        try {
            AuditDto auditDto = objectMapper.readValue(message, AuditDto.class);
            auditService.logAudit(auditDto);

            log.info("Method 'handleAuditLogEvent' completed successful");
        } catch (Exception e) {
            log.error("Method 'handleAuditLogEvent' failed {}", e.getMessage());
        }
    }
}
