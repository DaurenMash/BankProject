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
import org.springframework.beans.factory.annotation.Value;
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

    private static final String INVALID_DETAILS_MSG = "Invalid bank details.";

    @Value("${spring.kafka.topics.error-log.name}")
    String errorTopic;
    private final BankDetailsRepository bankDetailsRepository;
    private final BankDetailsMapper bankDetailsMapper;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final LicenseRepository licenseRepository;
    private final CertificateRepository certificateRepository;

    @Override
    @Transactional
    public BankDetailsDto createNewBankDetails(BankDetailsDto bankDetailsDto) {
        if (bankDetailsDto == null) {
            final IllegalArgumentException e = new IllegalArgumentException(INVALID_DETAILS_MSG);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final BankDetails bankDetails = bankDetailsMapper.toEntity(bankDetailsDto);
            final BankDetails savedBankDetails = bankDetailsRepository.save(bankDetails);
            final BankDetailsDto savedBankDetailsDto = bankDetailsMapper.toDto(savedBankDetails);
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
    public BankDetailsDto updateBankDetails(BankDetailsDto bankDetailsDto) throws ValidationException {
        final Long bankId = bankDetailsDto.getId();
        if (bankId == null) {
            final ValidationException e = new ValidationException(INVALID_DETAILS_MSG);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final BankDetails existingBankDetails = bankDetailsRepository.findById(bankId)
                    .orElseThrow(() -> {
                        log.error("No bank found with id {}", bankId);
                        globalExceptionHandler.handleException(
                                new EntityNotFoundException("No bank found with id: " + bankId), errorTopic);
                        return new EntityNotFoundException("No bank found with id " + bankId); // выбрасываем исключение
                    });
            bankDetailsMapper.updateFromDto(bankDetailsDto, existingBankDetails);
            final BankDetails updatedBankDetails = bankDetailsRepository.save(existingBankDetails);
            final BankDetailsDto savedBankDetailsDto = bankDetailsMapper.toDto(updatedBankDetails);
            log.info("Successfully updated bank details for bank ID: {}", bankId);
            return savedBankDetailsDto;
        } catch (DataAccessException | IllegalArgumentException e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteBankDetailsById(Long bankId) {
        if (bankId == null) {
            final IllegalArgumentException e = new IllegalArgumentException("Bank ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final BankDetails existingBankDetails = bankDetailsRepository.findById(bankId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no bank with id " + bankId));
            licenseRepository.deleteLicensesByBankDetailsId(bankId);
            certificateRepository.deleteCertificateByBankDetailsId(bankId);
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
            final IllegalArgumentException e = new IllegalArgumentException("Pageable must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final Page<BankDetails> bankDetailsPage = bankDetailsRepository.findAll(pageable);
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
            final IllegalArgumentException e = new IllegalArgumentException("Bank ID can't be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        return bankDetailsRepository.findById(bankId)
                .map(bankDetailsMapper::toDto)
                .orElseThrow(() -> {
                    final EntityNotFoundException e = new EntityNotFoundException("No bank with id " + bankId);
                    globalExceptionHandler.handleException(e, errorTopic);
                    log.info("Successfully retrieved bank details with ID: {}", bankId);
                    return e;
                });
    }

}
