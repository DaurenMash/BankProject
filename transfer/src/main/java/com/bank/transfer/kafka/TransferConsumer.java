package com.bank.transfer.kafka;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.exception.GlobalExceptionHandler;
import com.bank.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferConsumer {

    private final TransferService transferService;
    private final ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory;
    private final GlobalExceptionHandler exceptionHandler;

    // Временное хранилище для DTO с ожидаемым accountDetailsId
    private final Map<Long, Object> pendingTransfers = new ConcurrentHashMap<>();

    @KafkaListener(topics = "transfer.account", groupId = "transfer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAccountTransfer(AccountTransferDto dto) {
        try {
            if (dto.getAccountDetailsId() == null) {
                log.error("Missing accountDetailsId for account transfer: accountNumber={}", dto.getAccountNumber());
                throw new IllegalArgumentException("accountDetailsId is required");
            }
            pendingTransfers.put(dto.getAccountDetailsId(), dto);
            log.info("Stored account transfer pending accountDetailsId: accountNumber={}", dto.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to process account transfer: accountNumber={}", dto.getAccountNumber());
            exceptionHandler.handleException(e, null);
        }
    }

    @KafkaListener(topics = "transfer.card", groupId = "transfer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenCardTransfer(CardTransferDto dto) {
        try {
            if (dto.getAccountDetailsId() == null) {
                log.error("Missing accountDetailsId for card transfer: cardNumber={}", dto.getCardNumber());
                throw new IllegalArgumentException("accountDetailsId is required");
            }
            pendingTransfers.put(dto.getAccountDetailsId(), dto);
            log.info("Stored card transfer pending accountDetailsId: cardNumber={}", dto.getCardNumber());
        } catch (Exception e) {
            log.error("Failed to process card transfer: cardNumber={}", dto.getCardNumber());
            exceptionHandler.handleException(e, null);
        }
    }

    @KafkaListener(topics = "transfer.phone", groupId = "transfer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenPhoneTransfer(PhoneTransferDto dto) {
        try {
            if (dto.getAccountDetailsId() == null) {
                log.error("Missing accountDetailsId for phone transfer: phoneNumber={}", dto.getPhoneNumber());
                throw new IllegalArgumentException("accountDetailsId is required");
            }
            pendingTransfers.put(dto.getAccountDetailsId(), dto);
            log.info("Stored phone transfer pending accountDetailsId: phoneNumber={}", dto.getPhoneNumber());
        } catch (Exception e) {
            log.error("Failed to process phone transfer: phoneNumber={}", dto.getPhoneNumber());
            exceptionHandler.handleException(e, null);
        }
    }

    @KafkaListener(topics = "topicAccountDetailsGet", groupId = "transfer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAccountDetailsGet(Long accountDetailsId) {
        try {
            if (accountDetailsId == null) {
                log.warn("Received null accountDetailsId in topicAccountDetailsGet");
                return;
            }
            Object pendingDto = pendingTransfers.remove(accountDetailsId);
            if (pendingDto == null) {
                log.warn("No pending transfer found for accountDetailsId={}", accountDetailsId);
                return;
            }

            if (pendingDto instanceof AccountTransferDto dto) {
                dto.setAccountDetailsId(accountDetailsId); // На всякий случай подтверждаем
                transferService.saveAccountTransfer(dto);
                log.info("Processed account transfer for accountDetailsId={}", accountDetailsId);
            } else if (pendingDto instanceof CardTransferDto dto) {
                dto.setAccountDetailsId(accountDetailsId);
                transferService.saveCardTransfer(dto);
                log.info("Processed card transfer for accountDetailsId={}", accountDetailsId);
            } else if (pendingDto instanceof PhoneTransferDto dto) {
                dto.setAccountDetailsId(accountDetailsId);
                transferService.savePhoneTransfer(dto);
                log.info("Processed phone transfer for accountDetailsId={}", accountDetailsId);
            } else {
                log.warn("Unknown DTO type for accountDetailsId={}", accountDetailsId);
            }
        } catch (Exception e) {
            log.error("Failed to process accountDetailsId={}", accountDetailsId);
            exceptionHandler.handleException(e, null);
        }
    }
}