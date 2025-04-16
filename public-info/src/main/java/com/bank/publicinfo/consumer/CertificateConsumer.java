package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.service.CertificateService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateConsumer {

    private final CertificateService service;

    @KafkaListener(topics = "${spring.kafka.topics.certificate.create.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public CertificateDto creatingCertificateListening(CertificateDto certificateDto) {
        log.info("Received certificateDto to create: {}", certificateDto);
        try {
            final CertificateDto savedCertificate = this.service.createNewCertificate(certificateDto);
            log.info("New certificate saved successfully with ID: {}", savedCertificate.getId());
            return savedCertificate;
        } catch (Exception e) {
            log.error("Failed to save new certificate: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = "${spring.kafka.topics.certificate.update.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public CertificateDto updatingCertificateListening(CertificateDto certificateDto) {
        log.info("Received certificateDto to update: {}", certificateDto);
        final Long certificateId = certificateDto.getId();
        if (certificateId == null) {
            log.warn("Certificate ID is null, cannot update certificate.");
            throw new IllegalArgumentException("Certificate ID is null");
        }
        try {
            final CertificateDto updatedCertificate = this.service.updateCertificate(certificateDto);
            log.info("Certificate updated successfully with ID: {}", certificateId);
            return updatedCertificate;
        } catch (Exception e) {
            log.error("Failed to update certificate: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = "${spring.kafka.topics.certificate.delete.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public void deletingCertificateListening(CertificateDto certificateDto) {
        log.info("Received certificateDto to delete: {}", certificateDto);
        final Long certificateId = certificateDto.getId();
        if (certificateId == null) {
            log.warn("Certificate ID is null, cannot delete certificate.");
            return;
        }
        this.service.deleteCertificate(certificateId);
        log.info("Certificate deleted successfully with ID: {}", certificateId);
    }

    @KafkaListener(topics = {"${spring.kafka.topics.certificate.get.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "certificateKafkaListenerContainerFactory")
    public void gettingCertificateListening(CertificateDto certificateDto) {
        log.info("Received certificateDto to get: {}", certificateDto);
        if (certificateDto != null) {
            final Long certificateId = certificateDto.getId();
            if (certificateId == null) {
                log.warn("Certificate ID is null, cannot get certificate.");
                return;
            }
            try {
                final CertificateDto certificateToGet = this.service.getCertificateById(certificateId);
                log.info("Certificate retrieved successfully: {}", certificateToGet);
            } catch (Exception e) {
                log.error("Error retrieving certificate for ID {}: ", certificateId, e);
            }
        } else {
            log.error("Invalid certificate details received: null");
            throw new IllegalArgumentException("Invalid certificate details received");
        }
    }
}
