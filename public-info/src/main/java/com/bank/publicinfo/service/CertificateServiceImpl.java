package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.Certificate;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.exception.IllegalArgumentException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.mapper.CertificateMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateServiceImpl implements CertificateService {

    @Value("${spring.kafka.topics.error-log.name}")
    String errorTopic;
    private final CertificateRepository certificateRepository;
    private final CertificateMapper certificateMapper;
    private final BankDetailsRepository bankDetailsRepository;
    private final GlobalExceptionHandler globalExceptionHandler;


    private BankDetails findBankById(Long id) {
        return bankDetailsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bank with ID " + id + " not found"));
    }


    @Override
    @Transactional
    public CertificateDto createNewCertificate(CertificateDto CertificateDto) {
        if (CertificateDto == null) {
            log.error("Attempt to create null certificate");
            final IllegalArgumentException e = new IllegalArgumentException("Attempt to create null Certificate");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            final BankDetails bankDetails = findBankById(CertificateDto.getBankDetailsId());
            final Certificate certificate = certificateMapper.toEntity(CertificateDto);
            certificate.setBankDetails(bankDetails);
            final Certificate savedCertificate = certificateRepository.save(certificate);
            log.info("Successfully created new certificate with ID: {}", savedCertificate.getId());
            return certificateMapper.toDto(savedCertificate);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while creating a new Certificate.");
        }
    }


    @Override
    @Transactional
    public CertificateDto updateCertificate(CertificateDto newCertificateDto) {
        if (newCertificateDto == null) {
            log.error("Attempt to update Certificate with null DTO");
            final IllegalArgumentException e = new IllegalArgumentException("Certificate DTO must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final Certificate existingCertificate = certificateRepository.findById(newCertificateDto.getId())
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("No certificate with ID: " + newCertificateDto.getId());
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });

            final BankDetails bankDetails = findBankById(newCertificateDto.getBankDetailsId());
            certificateMapper.updateFromDto(newCertificateDto, existingCertificate);
            existingCertificate.setBankDetails(bankDetails);
            final Certificate savedCertificate = certificateRepository.save(existingCertificate);
            log.info("Successfully updated Certificate with ID: {}", savedCertificate.getId());
            return certificateMapper.toDto(savedCertificate);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while updating the Certificate.");
        }
    }


    @Override
    @Transactional
    public void deleteCertificate(Long CertificateId) {
        if (CertificateId == null) {
            log.error("Attempt to delete Certificate with null ID");
            final IllegalArgumentException e = new IllegalArgumentException("Certificate id must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            final Certificate existingCertificate = certificateRepository.findById(CertificateId)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("Certificate with ID not found " + CertificateId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            certificateRepository.delete(existingCertificate);
            log.info("Successfully deleted Certificate with ID: {}", CertificateId);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while deleting the Certificate.");
        }
    }


    @Override
    public List<CertificateDto> getCertificatesByBankDetails(Long bankDetailsId) {
        if (bankDetailsId == null) {
            log.error("Attempt to get Certificates with null bankDetails ID");
            final IllegalArgumentException e = new IllegalArgumentException("BankDetails ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            final BankDetails bankDetails = findBankById(bankDetailsId);
            final List<CertificateDto> certificates = certificateRepository
                    .findCertificatesByBankDetails(bankDetails)
                    .stream()
                    .map(certificateMapper::toDto)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} Certificates for BankDetails ID: {}",
                    certificates.size(), bankDetailsId);
            return certificates;
        } catch (DataAccessException e) {
            log.error("Data access error occurred while retrieving Certificates for BankDetails ID: {}",
                    bankDetailsId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while retrieving Certificates for BankDetails ID: {}",
                    bankDetailsId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving Certificates.");
        }
    }


    @Override
    public CertificateDto getCertificateById(Long certificateId) {
        if (certificateId == null) {
            log.error("Attempt to get Certificate details with null ID");
            final IllegalArgumentException e = new IllegalArgumentException("Certificate ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            final CertificateDto certificateDto = certificateRepository.findById(certificateId)
                    .map(certificateMapper::toDto)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("No certificate with ID " + certificateId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });

            log.info("Successfully retrieved Certificate with ID: {}", certificateId);
            return certificateDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving Certificate details.");
        }
    }


}
