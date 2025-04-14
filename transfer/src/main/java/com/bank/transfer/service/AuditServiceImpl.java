package com.bank.transfer.service;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.AuditDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.entity.Audit;
import com.bank.transfer.enums.EntityType;
import com.bank.transfer.enums.OperationType;
import com.bank.transfer.exception.GlobalExceptionHandler;
import com.bank.transfer.kafka.TransferProducer;
import com.bank.transfer.mapper.AuditMapper;
import com.bank.transfer.repository.AuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;
    private final GlobalExceptionHandler exceptionHandler;
    private final ObjectMapper objectMapper;
    private final TransferProducer transferProducer; // Добавляем зависимость

    @Override
    @Transactional(readOnly = true)
    public List<AuditDto> getAuditHistory() {
        try {
            List<Audit> audits = auditRepository.findAll();
            return audits.stream()
                    .map(auditMapper::toDto)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to retrieve audit history");
            exceptionHandler.handleException(e, null);
            throw new RuntimeException("Failed to retrieve audit history", e);
        }
    }

    @Override
    @Transactional
    public void auditAccountTransfer(AccountTransferDto dto) {
        Audit audit = saveAudit(EntityType.ACCOUNT_TRANSFER, dto);
        log.info("Audit recorded for account transfer: accountNumber={}", dto.getAccountNumber());
        AuditDto auditDto = auditMapper.toDto(audit);
        transferProducer.sendAuditHistory(auditDto); // Отправляем в audit.history
    }

    @Override
    @Transactional
    public void auditCardTransfer(CardTransferDto dto) {
        Audit audit = saveAudit(EntityType.CARD_TRANSFER, dto);
        log.info("Audit recorded for card transfer: cardNumber={}", dto.getCardNumber());
        AuditDto auditDto = auditMapper.toDto(audit);
        transferProducer.sendAuditHistory(auditDto); // Отправляем в audit.history
    }

    @Override
    @Transactional
    public void auditPhoneTransfer(PhoneTransferDto dto) {
        Audit audit = saveAudit(EntityType.PHONE_TRANSFER, dto);
        log.info("Audit recorded for phone transfer: phoneNumber={}", dto.getPhoneNumber());
        AuditDto auditDto = auditMapper.toDto(audit);
        transferProducer.sendAuditHistory(auditDto); // Отправляем в audit.history
    }

    private Audit saveAudit(EntityType entityType, Object entity) {
        AuditDto auditDto = new AuditDto();
        auditDto.setEntityType(entityType);
        auditDto.setOperationType(OperationType.CREATE);
        auditDto.setCreatedBy("system");
        auditDto.setCreatedAt(LocalDateTime.now());
        auditDto.setModifiedBy(null);
        auditDto.setModifiedAt(null);
        auditDto.setNewEntityJson(null);
        try {
            auditDto.setEntityJson(objectMapper.writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize entity to JSON for audit: entityType={}", entityType, e);
            auditDto.setEntityJson("{}");
        }

        Audit audit = auditMapper.toEntity(auditDto);
        auditRepository.save(audit);
        return audit; // Возвращаем сохраненный Audit для дальнейшего использования
    }
}