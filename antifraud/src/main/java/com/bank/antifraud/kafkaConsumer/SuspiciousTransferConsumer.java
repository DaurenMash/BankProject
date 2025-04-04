package com.bank.antifraud.kafkaConsumer;


import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.kafkaProducer.SuspiciousTransferProducer;
import com.bank.antifraud.service.SuspiciousTransferServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.annotation.KafkaListener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspiciousTransferConsumer {
    private final SuspiciousTransferServiceImpl transferService;
    private final SuspiciousTransferProducer kafkaProducer;

    @KafkaListener(topics = "suspicious-transfers.create", groupId = "anti_fraud-group", containerFactory = "suspiciousKafkaListenerContainerFactory")
    public void listenCard(
            @Payload Map<String, Object> massage,
            @Header(value = "amount") String amountHeader) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer id = (Integer) massage.get("id");
            BigDecimal amount;
            amount = new BigDecimal(amountHeader);
            if (id == null || id <= 0) {
                log.error("Invalid transfer_id: {}", id);
                throw new IllegalArgumentException("Invalid transfer ID: " + id);
            }

            if(massage.containsKey("phone_number")) {
                SuspiciousPhoneTransferDto phone = transferService.analyzePhoneTransfer(amount, id);
                if(phone == null) {
                    log.error("Analyze error phone transfer: {}", id);
                }
                response.put("isSuspicious", phone.isSuspicious());
                response.put("transfer_id", id);
            } else if (massage.containsKey("card_number")) {
                SuspiciousCardTransferDto card = transferService.analyzeCardTransfer(amount, id);
                if(card == null) {
                    log.error("Analyze error card transfer: {}", id);
                }
                response.put("isSuspicious", card.isSuspicious());
                response.put("transfer_id", id);
            } else if (massage.containsKey("account_number")) {
                SuspiciousAccountTransferDto account = transferService.analyzeAccountTransfer(amount, id);
                if(account == null) {
                    log.error("Analyze error account transfer: {}", id);
                }
                response.put("isSuspicious", account.isSuspicious());
                response.put("transfer_id", id);
            }
            kafkaProducer.eventResponse(response);
        } catch (Exception e) {
            log.error("Error processing transfer:", e);
        }
    }
}
