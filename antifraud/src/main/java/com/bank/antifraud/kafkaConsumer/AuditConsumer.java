package com.bank.antifraud.kafkaConsumer;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditConsumer {
    private final AuditService auditService;

    public AuditConsumer(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(topics = "audit-topic", groupId = "audit-group", containerFactory = "auditKafkaListenerContainerFactory")
    public void listen(AuditDto auditDto) {
    }
}
