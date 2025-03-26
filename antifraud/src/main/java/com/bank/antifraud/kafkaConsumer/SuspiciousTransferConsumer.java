package com.bank.antifraud.kafkaConsumer;


import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.service.KafkaProducerService;
import com.bank.antifraud.service.SuspiciousTransferServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SuspiciousTransferConsumer {
    private final ObjectMapper objectMapper;
    private final SuspiciousTransferServiceImpl transferService;
    private final KafkaProducerService kafkaProducer;

    public SuspiciousTransferConsumer(ObjectMapper objectMapper,
                                SuspiciousTransferServiceImpl transferService,
                                KafkaProducerService kafkaProducer) {
        this.objectMapper = objectMapper;
        this.transferService = transferService;
        this.kafkaProducer = kafkaProducer;
    }
    @KafkaListener(topics = "suspicious-transfers.create", groupId = "anti_fraud-group", containerFactory = "suspiciousKafkaListenerContainerFactory")
    public void listenCard(
            @Payload Map<String, Object> massage,
            @Header(value = "amount") String amountHeader) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer id = (Integer) massage.get("id");
            BigDecimal amount = null;
            amount = new BigDecimal(amountHeader);
            if(massage.containsKey("phone_number")) {
                SuspiciousPhoneTransferDto phone = transferService.analyzePhoneTransfer(amount, id);
                response.put("isSuspicious", phone.isSuspicious());
                response.put("transfer_id", id);
            } else if (massage.containsKey("card_number")) {
                SuspiciousCardTransferDto card = transferService.analyzeCardTransfer(amount, id);
                response.put("isSuspicious", card.isSuspicious());
                response.put("transfer_id", id);
            } else if (massage.containsKey("account_number")) {
                SuspiciousAccountTransferDto account = transferService.analyzeAccountTransfer(amount, id);
                response.put("isSuspicious", account.isSuspicious());
                response.put("transfer_id", id);
            }
            kafkaProducer.EventResponse(response);
        } catch (Exception e) {
            log.error("Error processing transfer {}: {}", e.getMessage());
        }
    }
}
