package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.ATMDto;
import com.bank.publicinfo.service.ATMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ATMConsumer {

    private final ATMService service;

    @KafkaListener(topics = {"${spring.kafka.topics.atm.create.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "atmKafkaListenerContainerFactory")
    public ATMDto creatingATMListening(ATMDto atmDto) {
        log.info("Received ATM to create: {}", atmDto.toString());
        try {
            final ATMDto savedATM = this.service.createNewATM(atmDto);
            log.info("New ATM saved successfully with ID: {}", savedATM.getId());
            return savedATM;
        } catch (Exception e) {
            log.error("Failed to save new ATM: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.atm.update.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "atmKafkaListenerContainerFactory")
    public ATMDto updatingATMListening(ATMDto atmDto) {
        log.info("Received ATM to update: {}", atmDto);
        final Long atmId = atmDto.getId();
        if (atmId == null) {
            log.warn("ATM ID is null, cannot update ATM.");
            throw new IllegalArgumentException("ATM id is null, cannot update ATM.");
        }
        try {
            final ATMDto updatedATM = this.service.updateATM(atmDto);
            log.info("ATM updated successfully with ID: {}", atmId);
            return updatedATM;
        } catch (Exception e) {
            log.error("Failed to update ATM: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.atm.delete.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "atmKafkaListenerContainerFactory")
    public void deletingATMListening(ATMDto atmDto) {
        log.info("Received ATM to delete: {}", atmDto);
        final Long atmId = atmDto.getId();
        if (atmId == null) {
            log.warn("ATM ID is null, cannot delete ATM.");
            return;
        }
        this.service.deleteATMById(atmId);
        log.info("ATM deleted successfully with ID: {}", atmId);
    }

    @KafkaListener(topics = {"${spring.kafka.topics.atm.get.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "atmKafkaListenerContainerFactory")
    public void gettingATMListening(ATMDto atmDto) {
        log.info("Received ATM request to get: {}", atmDto);
        if (atmDto != null) {
            final Long atmId = atmDto.getId();
            if (atmId == null) {
                log.warn("ATM ID is null, cannot get ATM.");
                return;
            }
            try {
                final ATMDto retrievedATM = this.service.getATMById(atmId);
                log.info("ATM retrieved successfully: {}", retrievedATM);
            } catch (Exception e) {
                log.error("Error retrieving ATM for ID {}: ", atmId, e);
            }
        } else {
            log.error("Invalid ATM received: null");
            throw new IllegalArgumentException("Invalid ATM received");
        }
    }
}
