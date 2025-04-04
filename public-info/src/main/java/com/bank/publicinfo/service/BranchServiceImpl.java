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

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    private final AtmRepository atmRepository;
    private final GlobalExceptionHandler globalExceptionHandler;

    String errorTopic = "public-info.error.logs";

    @Override
    @Transactional
    public BranchDto createNewBranch(BranchDto branchDto) {
        if (branchDto == null) {
            log.error("Attempt to create null branch");
            IllegalArgumentException e = new IllegalArgumentException("Branch must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            Branch branch = branchMapper.toEntity(branchDto);
            Branch savedBranch = branchRepository.save(branch);
            log.info("Successfully created new branch with ID: {}", savedBranch.getId());
            return branchMapper.toDto(savedBranch);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while creating a new branch.");
        }
    }


    @Override
    @Transactional
    public BranchDto updateBranch(Long branchId, BranchDto branchDto) {
        if (branchId == null) {
            log.error("Attempt to update branch with null ID");
            IllegalArgumentException e = new IllegalArgumentException("Branch ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        if (branchDto == null) {
            log.error("Attempt to update branch with null DTO");
            IllegalArgumentException e = new IllegalArgumentException("Branch DTO must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            Branch existingBranch = branchRepository.findById(branchId)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("There is no branch with id " + branchId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            branchMapper.updateFromDto(branchDto, existingBranch);
            log.info("Updating branch for ID: {}", branchId);
            Branch updatedBranch = branchRepository.save(existingBranch);
            log.info("Successfully updated branch for ID: {}", branchId);
            return branchMapper.toDto(updatedBranch);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while updating branch details.");
        }
    }


    @Override
    @Transactional
    public void deleteBranchById(Long branchId) {
        if (branchId == null) {
            log.error("Attempt to delete branch with null ID");
            IllegalArgumentException e = new IllegalArgumentException("Branch ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("There is no branch with id " + branchId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });

            Set<ATM> atms = branch.getAtms();
            atmRepository.deleteAll(atms);
            branchRepository.delete(branch);
            log.info("Successfully deleted branch with ID: {}", branchId);
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while deleting branch.");
        }
    }


    @Override
    public List<BranchDto> getAllBranches(Pageable pageable) {
        if (pageable == null) {
            log.error("Attempt to get all branches with null Pageable");
            IllegalArgumentException e = new IllegalArgumentException("Pageable must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            Page<Branch> branchPage = branchRepository.findAll(pageable);
            List<BranchDto> branches = branchPage.stream()
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
            throw new RuntimeException("An unexpected error occurred while retrieving all branches.", e);
        }
    }


    @Override
    public BranchDto getBranchById(Long branchId) {
        if (branchId == null) {
            log.error("Attempt to get branch details with null ID");
            IllegalArgumentException e = new IllegalArgumentException("Branch ID must not be null");
            globalExceptionHandler.handleException(e, errorTopic);
            throw e;
        }
        try {
            BranchDto branchDto = branchRepository.findById(branchId)
                    .map(branchMapper::toDto)
                    .orElseThrow(() -> {
                        EntityNotFoundException e = new EntityNotFoundException("There is no branch with id " + branchId);
                        globalExceptionHandler.handleException(e, errorTopic);
                        return e;
                    });
            log.info("Successfully retrieved branch with ID: {}", branchId);
            return branchDto;
        } catch (Exception e) {
            globalExceptionHandler.handleException(e, errorTopic);
            throw new RuntimeException("An unexpected error occurred while retrieving branch details.");
        }
    }


}