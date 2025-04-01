package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.ATMDto;
import com.bank.publicinfo.entity.ATM;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.exception.IllegalArgumentException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.mapper.ATMMapper;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.BranchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ATMServiceImpl implements ATMService {

    private final AtmRepository atmRepository;
    private final ATMMapper atmMapper;
    private final BranchRepository branchRepository;
    private final GlobalExceptionHandler globalExceptionHandler;

    String errorTopic = "public-info.error.logs";


    @Override
    @Transactional
    public ATMDto createNewATM(ATMDto atmDto) {
        if (atmDto == null) {
            log.error("Attempt to create null ATM");
            IllegalArgumentException e = new IllegalArgumentException("Attempt to create null ATM");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            Branch branch = branchRepository.findById(atmDto.getBranchId())
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found for id: " + atmDto.getBranchId()));
            ATM atm = atmMapper.toEntity(atmDto);
            atm.setBranch(branch);
            ATM savedAtm = atmRepository.save(atm);
            ATMDto savedAtmDto = atmMapper.toDto(savedAtm);
            log.info("Successfully created new ATM with ID: {}", savedAtm.getId());

            return savedAtmDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while creating a new ATM.");
        }
    }


    @Override
    @Transactional
    public ATMDto updateATM(ATMDto atmDto) {
        if (atmDto == null) {
            log.error("Attempt to update ATM with null DTO");
            IllegalArgumentException e = new IllegalArgumentException("ATM DTO must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            ATM existingAtm = atmRepository.findById(atmDto.getId())
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("ATM not found with ID: " + atmDto.getId());
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });

            Branch branch = branchRepository.findById(atmDto.getBranchId())
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("Branch not found for id: " + atmDto.getBranchId());
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });

            atmMapper.updateFromDto(atmDto, existingAtm);
            existingAtm.setBranch(branch);
            ATM savedAtm = atmRepository.save(existingAtm);
            ATMDto savedAtmDto = atmMapper.toDto(savedAtm);
            log.info("Successfully updated ATM with ID: {}", savedAtm.getId());
            return savedAtmDto;

        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while updating branch details.");
        }
    }


    @Override
    @Transactional
    public void deleteATMById(Long atmId) {
        if (atmId == null) {
            log.error("Attempt to delete ATM with null ID");
            IllegalArgumentException e = new IllegalArgumentException("ATM ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            ATM existingATM = atmRepository.findById(atmId)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("There is no ATM with id " + atmId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            atmRepository.delete(existingATM);
            log.info("Successfully deleted ATM with ID: {}", atmId);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while deleting ATM.");
        }
    }


    @Override
    public List<ATMDto> getATMs(Long branchId) {
        if (branchId == null) {
            log.error("Attempt to get ATMs with null branch ID");
            IllegalArgumentException e = new IllegalArgumentException("Branch ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            List<ATM> atms = atmRepository.findByBranchId(branchId);
            List<ATMDto> atmDtos = atms.stream()
                    .map(atmMapper::toDto)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} ATMs for branch ID: {}", atmDtos.size(), branchId);
            return atmDtos;
        } catch (DataAccessException e) {
            log.error("Data access error occurred while retrieving ATMs for branch ID: {}", branchId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while retrieving ATMs for branch ID: {}", branchId, e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving ATMs.", e);
        }
    }


    @Override
    public ATMDto getATMById(Long atmId) {
        if (atmId == null) {
            log.error("Attempt to get ATM details with null ID");
            IllegalArgumentException e = new IllegalArgumentException("ATM ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }

        try {
            ATMDto atmDto = atmRepository.findById(atmId)
                    .map(atmMapper::toDto)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("There is no ATM with id " + atmId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            log.info("Successfully retrieved ATM with ID: {}", atmId);
            return atmDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving ATM details.");
        }
    }


}




