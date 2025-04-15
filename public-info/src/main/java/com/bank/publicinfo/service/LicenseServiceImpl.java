package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.License;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.exception.IllegalArgumentException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.mapper.LicenseMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.LicenseRepository;
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
public class LicenseServiceImpl implements LicenseService {

    private static final String NOT_FOUND = " not found";
    private static final String NULL_LICENSE_MESSAGE = "Attempt to create null License";
    private static final String NULL_LICENSE_ID_MESSAGE = "License ID must not be null";
    @Value("${spring.kafka.topics.error-log.name}")
    String errorTopic;
    private final LicenseRepository licenseRepository;
    private final LicenseMapper licenseMapper;
    private final BankDetailsRepository bankDetailsRepository;
    private final GlobalExceptionHandler globalExceptionHandler;

    private BankDetails findBankById(Long id) {
        return bankDetailsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bank with ID " + id + NOT_FOUND));
    }

    @Override
    @Transactional
    public LicenseDto createNewLicense(LicenseDto licenseDto) {
        if (licenseDto == null) {
            final IllegalArgumentException e = new IllegalArgumentException(NULL_LICENSE_MESSAGE);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final BankDetails bankDetails = findBankById(licenseDto.getBankDetailsId());
            final License license = licenseMapper.toEntity(licenseDto);
            license.setBankDetails(bankDetails);
            final License savedLicense = licenseRepository.save(license);
            log.info("Successfully created new License with ID: {}", savedLicense.getId());
            return licenseMapper.toDto(savedLicense);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional
    public LicenseDto updateLicense(LicenseDto newLicenseDto) {
        if (newLicenseDto == null) {
            final IllegalArgumentException e = new IllegalArgumentException("License DTO must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final License existingLicense = licenseRepository.findById(newLicenseDto.getId())
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("License with ID " + newLicenseDto.getId() + NOT_FOUND);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            final BankDetails bankDetails = findBankById(newLicenseDto.getBankDetailsId());
            licenseMapper.updateFromDto(newLicenseDto, existingLicense);
            existingLicense.setBankDetails(bankDetails);
            final License savedLicense = licenseRepository.save(existingLicense);
            log.info("Successfully updated License with ID: {}", savedLicense.getId());
            return licenseMapper.toDto(savedLicense);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteLicense(Long licenseId) {
        if (licenseId == null) {
            final IllegalArgumentException e = new IllegalArgumentException(NULL_LICENSE_ID_MESSAGE);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final License existingLicense = licenseRepository.findById(licenseId)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("No license with ID " + licenseId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            licenseRepository.delete(existingLicense);
            log.info("Successfully deleted License with ID: {}", licenseId);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    public List<LicenseDto> getLicensesByBankDetails(Long bankDetailsId) {
        if (bankDetailsId == null) {
            final IllegalArgumentException e = new IllegalArgumentException("BankDetails ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final BankDetails bankDetails = findBankById(bankDetailsId);
            final List<LicenseDto> licenses = licenseRepository
                    .findLicensesByBankDetails(bankDetails)
                    .stream()
                    .map(licenseMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Successfully retrieved {} Licenses for BankDetails ID: {}", licenses.size(), bankDetailsId);
            return licenses;
        } catch (DataAccessException e) {
            log.error("Data access error occurred while retrieving Licenses for BankDetails ID: {}", bankDetailsId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error while retrieving Licenses for BankDetails ID: {}", bankDetailsId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    public LicenseDto getLicenseById(Long licenseId) {
        if (licenseId == null) {
            final IllegalArgumentException e = new IllegalArgumentException(NULL_LICENSE_ID_MESSAGE);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final LicenseDto licenseDto = licenseRepository.findById(licenseId)
                    .map(licenseMapper::toDto)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("License with id " + licenseId + NOT_FOUND);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            log.info("Successfully retrieved License with ID: {}", licenseId);
            return licenseDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }
}
