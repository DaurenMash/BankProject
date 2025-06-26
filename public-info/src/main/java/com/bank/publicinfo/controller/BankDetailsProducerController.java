package com.bank.publicinfo.controller;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.exception.ValidationException;
import com.bank.publicinfo.producer.BankDetailsProducer;
import com.bank.publicinfo.service.BankDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;


@RestController
@RequestMapping("/api/bank-details")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "BankDetailsController", description = "API для управления банковскими данными")
public class BankDetailsProducerController {

    private final BankDetailsService service;
    private final BankDetailsProducer bankDetailsProducer;

    @Value("${spring.kafka.topics.bank.create.name}")
    private String createTopic;

    @Value("${spring.kafka.topics.bank.update.name}")
    private String updateTopic;

    @Value("${spring.kafka.topics.bank.delete.name}")
    private String deleteTopic;

    @Value("${spring.kafka.topics.bank.get.name}")
    private String getTopic;

    @Operation(summary = "Создать новые банковские данные")
    @PostMapping
    public ResponseEntity<BankDetailsDto> createBankDetails(
            @RequestBody @Valid BankDetailsDto bankDetailsDto) throws ValidationException {
        BankDetailsDto createdDetails = service.createNewBankDetails(bankDetailsDto);
        bankDetailsProducer.sendBankDetails(createTopic, createdDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDetails);
    }

    @Operation(summary = "Обновить банковские данные")
    @PutMapping
    public ResponseEntity<BankDetailsDto> updateBankDetails(
            @RequestBody @Valid BankDetailsDto bankDetailsDto) throws ValidationException {
        BankDetailsDto updatedDetails = service.updateBankDetails(bankDetailsDto);
        bankDetailsProducer.sendBankDetails(updateTopic, updatedDetails);
        return ResponseEntity.ok(updatedDetails);
    }

    @Operation(summary = "Удалить банковские данные по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankDetails(@PathVariable Long id) throws ValidationException {
        service.deleteBankDetailsById(id);
        bankDetailsProducer.sendLongValue(deleteTopic, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить банковские данные по ID")
    @GetMapping("/{id}")
    public ResponseEntity<BankDetailsDto> getBankDetails(@PathVariable Long id) throws ValidationException {
        BankDetailsDto details = service.getBankDetailsById(id);
        bankDetailsProducer.sendLongValue(getTopic, id);
        return ResponseEntity.ok(details);
    }

}
