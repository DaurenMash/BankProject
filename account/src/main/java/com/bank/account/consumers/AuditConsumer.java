package com.bank.account.consumers;

import com.bank.account.dto.AuditDto;
import com.bank.account.service.AuditService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuditConsumer {
    private final AuditService auditService;

    public AuditConsumer(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(topics = "audit.logs", groupId = "audit-group")
    public void handleAuditLogEvent(AuditDto auditDto) {
        auditService.logAudit(auditDto);
    }
}
