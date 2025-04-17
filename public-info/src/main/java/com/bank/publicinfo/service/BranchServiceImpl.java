package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.entity.ATM;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.exception.IllegalArgumentException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.mapper.BranchMapper;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchServiceImpl implements BranchService {

    @Value("${spring.kafka.topics.error-log.name}")
    String errorTopic;
    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private final AtmRepository atmRepository;
    private final GlobalExceptionHandler globalExceptionHandler;

    @Override
    @Transactional
    public BranchDto createNewBranch(BranchDto branchDto) {
        if (branchDto == null) {
            final IllegalArgumentException e = new IllegalArgumentException("Branch must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final Branch branch = branchMapper.toEntity(branchDto);
            final Branch savedBranch = branchRepository.save(branch);
            log.info("Successfully created new branch with ID: {}", savedBranch.getId());
            return branchMapper.toDto(savedBranch);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional
    public BranchDto updateBranch(Long branchId, BranchDto branchDto) {
        if (branchId == null) {
            final IllegalArgumentException e = new IllegalArgumentException("Branch ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        if (branchDto == null) {
            final IllegalArgumentException e = new IllegalArgumentException("Branch DTO must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final Branch existingBranch = branchRepository.findById(branchId)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("There is no branch with id " + branchId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            branchMapper.updateFromDto(branchDto, existingBranch);
            log.info("Updating branch for ID: {}", branchId);
            final Branch updatedBranch = branchRepository.save(existingBranch);
            log.info("Successfully updated branch for ID: {}", branchId);
            return branchMapper.toDto(updatedBranch);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteBranchById(Long branchId) {
        if (branchId == null) {
            final IllegalArgumentException e = new IllegalArgumentException("Branch ID can't be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("No branch with id " + branchId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            final Set<ATM> atms = branch.getAtms();
            atmRepository.deleteAll(atms);
            branchRepository.delete(branch);
            log.info("Successfully deleted branch with ID: {}", branchId);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    public List<BranchDto> getAllBranches(Pageable pageable) {
        if (pageable == null) {
            final IllegalArgumentException e = new IllegalArgumentException("Pageable must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final Page<Branch> branchPage = branchRepository.findAll(pageable);
            final List<BranchDto> branches = branchPage.stream()
                    .map(branchMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Successfully retrieved {} branches", branches.size());
            return branches;
        } catch (DataAccessException e) {
            log.error("Data access error occurred while retrieving branches", e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while retrieving all branches", e);
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }

    @Override
    public BranchDto getBranchById(Long branchId) {
        if (branchId == null) {
            final IllegalArgumentException e = new IllegalArgumentException("Branch id must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            final BranchDto branchDto = branchRepository.findById(branchId)
                    .map(branchMapper::toDto)
                    .orElseThrow(() -> {
                        final EntityNotFoundException e =
                                new EntityNotFoundException("No any branch with id " + branchId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            log.info("Successfully retrieved branch with ID: {}", branchId);
            return branchDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
    }
}
