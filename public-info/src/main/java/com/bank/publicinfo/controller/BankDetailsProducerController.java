package com.bank.publicinfo.controller;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.producer.BankDetailsProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "BankDetailsProducer", description = "API для отправки банковских данных в Kafka")
public class BankDetailsProducerController {

    private final BankDetailsProducer bankDetailsProducer;

    @Operation(summary = "Отправить банковские данные в Kafka")
    @PostMapping("/send")
    public ResponseEntity<String> sendBankDetails(
            @RequestParam String topic,
            @RequestBody BankDetailsDto bankDetailsDto) {
        bankDetailsProducer.sendBankDetails(topic, bankDetailsDto);
        log.info("Bank details sent to topic: {}", topic);
        return ResponseEntity.ok("Bank details sent to topic: " + topic);
    }
}

