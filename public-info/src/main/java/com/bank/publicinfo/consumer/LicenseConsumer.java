package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.service.LicenseService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenseConsumer {

    private final LicenseService service;

    @KafkaListener(topics = {"${spring.kafka.topics.license.create.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public LicenseDto creatingLicenseListening(LicenseDto licenseDto) {
        log.info("Received licenseDto to create: {}", licenseDto);
        try {
            final LicenseDto savedLicense = this.service.createNewLicense(licenseDto);
            log.info("New license saved successfully with ID: {}", savedLicense.getId());
            return savedLicense;
        } catch (Exception e) {
            log.error("Failed to save new license: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.license.update.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public LicenseDto updatingLicenseListening(LicenseDto licenseDto) {
        log.info("Received licenseDto to update: {}", licenseDto);
        final Long licenseId = licenseDto.getId();
        if (licenseId == null) {
            log.warn("License ID is null, cannot update license.");
            throw new IllegalArgumentException("License ID is null");
        }
        try {
            final LicenseDto updatedLicense = this.service.updateLicense(licenseDto);
            log.info("License updated successfully with ID: {}", licenseId);
            return updatedLicense;
        } catch (Exception e) {
            log.error("Failed to update license: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.license.delete.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public void deletingLicenseListening(LicenseDto licenseDto) {
        log.info("Received licenseDto to delete: {}", licenseDto);
        final Long licenseId = licenseDto.getId();
        if (licenseId == null) {
            log.warn("License ID is null, cannot delete license.");
            return;
        }
        this.service.deleteLicense(licenseId);
        log.info("License deleted successfully with ID: {}", licenseId);
    }

    @KafkaListener(topics = {"${spring.kafka.topics.license.get.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public void gettingLicenseListening(LicenseDto licenseDto) {
        log.info("Received licenseDto to get: {}", licenseDto);
        if (licenseDto != null) {
            final Long licenseId = licenseDto.getId();
            if (licenseId == null) {
                log.warn("License ID is null, cannot get license.");
                return;
            }
            try {
                final LicenseDto licenseToGet = this.service.getLicenseById(licenseId);
                log.info("License retrieved successfully: {}", licenseToGet);
            } catch (Exception e) {
                log.error("Error retrieving license for ID {}: ", licenseId, e);
            }
        } else {
            log.error("Invalid license details received: null");
            throw new IllegalArgumentException("Invalid license details received");
        }
    }
}
