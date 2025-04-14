package com.bank.transfer.kafka;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.exception.GlobalExceptionHandler;
import com.bank.transfer.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditConsumer {

    private final AuditService auditService;
    private final ConcurrentKafkaListenerContainerFactory<String, Object> auditKafkaListenerContainerFactory;
    private final GlobalExceptionHandler exceptionHandler;

    @KafkaListener(topics = "transfer.account", groupId = "audit-group",
            containerFactory = "auditKafkaListenerContainerFactory")
    public void auditAccountTransfer(AccountTransferDto dto) {
        try {
            auditService.auditAccountTransfer(dto);
        } catch (Exception e) {
            log.error("Failed to record audit for account transfer: accountNumber={}", dto.getAccountNumber());
            exceptionHandler.handleException(e, null);
        }
    }

    @KafkaListener(topics = "transfer.card", groupId = "audit-group",
            containerFactory = "auditKafkaListenerContainerFactory")
    public void auditCardTransfer(CardTransferDto dto) {
        try {
            auditService.auditCardTransfer(dto);
        } catch (Exception e) {
            log.error("Failed to record audit for card transfer: cardNumber={}", dto.getCardNumber());
            exceptionHandler.handleException(e, null);
        }
    }

    @KafkaListener(topics = "transfer.phone", groupId = "audit-group",
            containerFactory = "auditKafkaListenerContainerFactory")
    public void auditPhoneTransfer(PhoneTransferDto dto) {
        try {
            auditService.auditPhoneTransfer(dto);
        } catch (Exception e) {
            log.error("Failed to record audit for phone transfer: phoneNumber={}", dto.getPhoneNumber());
            exceptionHandler.handleException(e, null);
        }
    }
}