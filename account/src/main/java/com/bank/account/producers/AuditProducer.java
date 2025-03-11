package com.bank.account.producers;

import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuditProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuditLogEvent(AuditDto auditDto) {
        kafkaTemplate.send("audit.logs", auditDto);
    }
}
