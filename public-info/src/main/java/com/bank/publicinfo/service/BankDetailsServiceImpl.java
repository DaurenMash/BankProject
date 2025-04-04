package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.exception.ValidationException;
import com.bank.publicinfo.exception.IllegalArgumentException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.mapper.BankDetailsMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import com.bank.publicinfo.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BankDetailsServiceImpl implements BankDetailsService {

    private final BankDetailsRepository bankDetailsRepository;
    private final BankDetailsMapper bankDetailsMapper;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final LicenseRepository licenseRepository;
    private final CertificateRepository certificateRepository;

    String errorTopic = "public-info.error.logs";

    @Override
    @Transactional
    public BankDetailsDto createNewBankDetails(BankDetailsDto bankDetailsDto) {
        if (bankDetailsDto == null) {
            log.error("Attempt to create null bank details");
            IllegalArgumentException e = new IllegalArgumentException("Invalid bank details");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            BankDetails bankDetails = bankDetailsMapper.toEntity(bankDetailsDto);
            BankDetails savedBankDetails = bankDetailsRepository.save(bankDetails);
            BankDetailsDto savedBankDetailsDto = bankDetailsMapper.toDto(savedBankDetails);
            log.info("Bank details created successfully for bank details: {}", savedBankDetailsDto);
            return savedBankDetailsDto;
        } catch (DataAccessException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while updating bank details.");
        }
    }

    @Override
    @Transactional
    public BankDetailsDto updateBankDetails(BankDetailsDto bankDetailsDto) {
        Long bankId = bankDetailsDto.getId();
        if (bankId == null) {
            log.error("Attempt to update bank details with null ID");
            globalExceptionHandler.handleException(new ValidationException("Attempt to update bank details with null ID"),
                    errorTopic);
        }
        try {
            assert bankId != null;
            BankDetails existingBankDetails = bankDetailsRepository.findById(bankId)
                    .orElseThrow(() -> {
                        log.error("No bank found with id {}", bankId);
                        globalExceptionHandler.handleException(new EntityNotFoundException("No bank found with id: " + bankId),
                                errorTopic);
                        return new EntityNotFoundException("No bank found with id " + bankId);
                    });
            bankDetailsMapper.updateFromDto(bankDetailsDto, existingBankDetails);
            log.info("Updating bank details for bank ID: {}", bankId);

            BankDetails updatedBankDetails = bankDetailsRepository.save(existingBankDetails);
            BankDetailsDto savedBankDetailsDto = bankDetailsMapper.toDto(updatedBankDetails);

            log.info("Successfully updated bank details for bank ID: {}", bankId);
            return savedBankDetailsDto;
        } catch (DataAccessException e) {
            globalExceptionHandler.handleException(e, "public-info.error.logs");
            throw e;
        } catch (IllegalArgumentException e) {
            globalExceptionHandler.handleException(e, "public-info.error.logs");
            throw new IllegalArgumentException("Illegal argument: " + e.getMessage());
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, "public-info.error.logs");
            throw new RuntimeException("An unexpected error occurred while updating bank details.");
        }
    }


    @Override
    @Transactional
    public void deleteBankDetailsById(Long bankId) {
        if (bankId == null) {
            log.error("Attempt to delete bank details with null ID");
            IllegalArgumentException e = new IllegalArgumentException("Bank ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            BankDetails existingBankDetails = bankDetailsRepository.findById(bankId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no bank with id " + bankId));
            licenseRepository.deleteLicensesByBankDetails_Id(bankId);
            certificateRepository.deleteCertificateByBankDetails_Id(bankId);
            bankDetailsRepository.delete(existingBankDetails);

            log.info("Successfully deleted bank details with ID: {}", bankId);
        } catch (EntityNotFoundException | DataAccessException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while deleting bank details.");
        }
    }


    @Override
    public List<BankDetailsDto> getAllBanksDetails(Pageable pageable) {
        if (pageable == null) {
            log.error("Attempt to get all bank details with null Pageable");
            IllegalArgumentException e = new IllegalArgumentException("Pageable must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            Page<BankDetails> bankDetailsPage = bankDetailsRepository.findAll(pageable);
            return bankDetailsPage.stream()
                    .map(bankDetailsMapper::toDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving all bank details.");
        }
    }


    @Override
    public BankDetailsDto getBankDetailsById(Long bankId) {
        if (bankId == null) {
            log.error("Attempt to get bank details with null ID");
            IllegalArgumentException e = new IllegalArgumentException("Bank ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            BankDetailsDto bankDetailsDto = bankDetailsRepository.findById(bankId)
                    .map(bankDetailsMapper::toDto)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("There is no bank with id " + bankId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            log.info("Successfully retrieved bank details with ID: {}", bankId);
            return bankDetailsDto;
        } catch (DataAccessException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving bank details.");
        }
    }


}