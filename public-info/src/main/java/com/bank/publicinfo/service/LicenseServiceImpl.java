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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final LicenseMapper licenseMapper;
    private final BankDetailsRepository bankDetailsRepository;
    private final GlobalExceptionHandler globalExceptionHandler;

    String errorTopic = "public-info.error.logs";

    private BankDetails findBankById(Long id) {
        return bankDetailsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bank with ID " + id + " not found"));
    }


    @Override
    @Transactional
    public LicenseDto createNewLicense(LicenseDto licenseDto) {
        if (licenseDto == null) {
            log.error("Attempt to create null License");
            IllegalArgumentException e = new IllegalArgumentException("Attempt to create null License");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            BankDetails bankDetails = findBankById(licenseDto.getBankDetailsId());
            License license = licenseMapper.toEntity(licenseDto);
            license.setBankDetails(bankDetails);
            License savedLicense = licenseRepository.save(license);
            log.info("Successfully created new License with ID: {}", savedLicense.getId());
            return licenseMapper.toDto(savedLicense);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while creating a new License.");
        }
    }


    @Override
    @Transactional
    public LicenseDto updateLicense(LicenseDto newLicenseDto) {
        if (newLicenseDto == null) {
            log.error("Attempt to update License with null DTO");
            IllegalArgumentException e = new IllegalArgumentException("License DTO must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            License existingLicense = licenseRepository.findById(newLicenseDto.getId())
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("License with ID " + newLicenseDto.getId() + " not found");
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });

            BankDetails bankDetails = findBankById(newLicenseDto.getBankDetailsId());
            licenseMapper.updateFromDto(newLicenseDto, existingLicense);
            existingLicense.setBankDetails(bankDetails);
            License savedLicense = licenseRepository.save(existingLicense);
            log.info("Successfully updated License with ID: {}", savedLicense.getId());
            return licenseMapper.toDto(savedLicense);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while updating the License.");
        }
    }


    @Override
    @Transactional
    public void deleteLicense(Long licenseId) {
        if (licenseId == null) {
            log.error("Attempt to delete License with null ID");
            IllegalArgumentException e = new IllegalArgumentException("License ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            License existingLicense = licenseRepository.findById(licenseId)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("License with ID " + licenseId + " not found");
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            licenseRepository.delete(existingLicense);
            log.info("Successfully deleted License with ID: {}", licenseId);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while deleting the License.");
        }
    }


    @Override
    public List<LicenseDto> getLicensesByBankDetails(Long bankDetailsId) {
        if (bankDetailsId == null) {
            log.error("Attempt to get Licenses with null bankDetails ID");
            IllegalArgumentException e = new IllegalArgumentException("BankDetails ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            BankDetails bankDetails = findBankById(bankDetailsId);
            List<LicenseDto> licenses = licenseRepository
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
            log.error("An unexpected error occurred while retrieving Licenses for BankDetails ID: {}", bankDetailsId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving Licenses.");
        }
    }


    @Override
    public LicenseDto getLicenseById(Long licenseId) {
        if (licenseId == null) {
            log.error("Attempt to get License details with null ID");
            IllegalArgumentException e = new IllegalArgumentException("License ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            LicenseDto licenseDto = licenseRepository.findById(licenseId)
                    .map(licenseMapper::toDto)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("License with ID " + licenseId + " not found");
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });

            log.info("Successfully retrieved License with ID: {}", licenseId);
            return licenseDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving License details.");
        }
    }


}
