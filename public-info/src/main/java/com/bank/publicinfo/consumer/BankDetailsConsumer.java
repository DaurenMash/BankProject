package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.exception.ValidationException;
import com.bank.publicinfo.exception.IllegalArgumentException;
import com.bank.publicinfo.service.BankDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankDetailsConsumer {

    private final BankDetailsService service;

    @KafkaListener(topics = {"${spring.kafka.topics.bank.create.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public BankDetailsDto creatingBankListening(BankDetailsDto bankDetailsDto) throws ValidationException {
        log.info("Received bankDetailsDto to create: {}", bankDetailsDto.toString());
        try {
            final BankDetailsDto savedBankDetails = this.service.createNewBankDetails(bankDetailsDto);
            log.info("New bank details saved successfully with ID: {}", savedBankDetails.getId());
            return savedBankDetails;
        } catch (Exception e) {
            log.error("Failed to save new bank details: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.bank.update.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public BankDetailsDto updatingBankListening(BankDetailsDto bankDetailsDto) throws ValidationException {
        log.info("Received bankDetailsDto to update: {}", bankDetailsDto.toString());
        final Long bankId = bankDetailsDto.getId();
        if (bankId == null) {
            log.warn("Bank ID is null, cannot update bank details.");
            throw new IllegalArgumentException("Bank ID is null");
        }
        try {
            final BankDetailsDto updatedBankDetails = this.service.updateBankDetails(bankDetailsDto);
            log.info("Bank details updated successfully with ID: {}", bankId);
            return updatedBankDetails;
        } catch (Exception e) {
            log.error("Failed to update bank details: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.bank.delete.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void deletingBankListening(BankDetailsDto bankDetailsDto) throws ValidationException {
        log.info("Received bankDetailsDto to delete: {}", bankDetailsDto.toString());
        final Long bankId = bankDetailsDto.getId();
        if (bankId == null) {
            log.warn("Bank ID is null, cannot delete bank details.");
            return;
        }
        this.service.deleteBankDetailsById(bankId);
        log.info("Bank details deleted successfully with ID: {}", bankId);
    }

    @KafkaListener(topics = {"${spring.kafka.topics.bank.get.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void gettingBankListening(BankDetailsDto bankDetailsDto) throws ValidationException {
        log.info("Received bankDetailsDto to get: {}", bankDetailsDto);
        if (bankDetailsDto != null) {
            final Long bankId = bankDetailsDto.getId();
            if (bankId == null) {
                log.warn("Bank ID is null, cannot get bank details.");
                return;
            }
            try {
                final BankDetailsDto bankDetailsDtoToGet = this.service.getBankDetailsById(bankId);
                log.info("Bank details retrieved successfully: {}", bankDetailsDtoToGet);
            } catch (Exception e) {
                log.error("Error retrieving bank details for ID {}: ", bankId, e);
            }
        } else {
            log.error("Invalid bank details received: null");
            throw new ValidationException("Invalid bank details received");
        }
    }
}
