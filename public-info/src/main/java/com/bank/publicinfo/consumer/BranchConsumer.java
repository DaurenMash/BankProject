package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.bank.publicinfo.exception.ValidationException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchConsumer {

    private final BranchService service;

    @KafkaListener(topics = {"${spring.kafka.topics.branch.create.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "branchKafkaListenerContainerFactory")
    public BranchDto creatingBranchListening(BranchDto branchDto) throws javax.xml.bind.ValidationException {
        log.info("Received branch to create: {}", branchDto.toString());
        try {
            final BranchDto savedBranch = this.service.createNewBranch(branchDto);
            log.info("New branch saved successfully with ID: {}", savedBranch.getId());
            return savedBranch;
        } catch (Exception e) {
            log.error("Failed to save new Branch: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.branch.update.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "branchKafkaListenerContainerFactory")
    public BranchDto updatingBranchListening(BranchDto branchDto) {
        log.info("Received branch to update: {}", branchDto.toString());
        final Long branchId = branchDto.getId();
        if (branchId == null) {
            log.warn("Branch is null, cannot update Branch.");
            throw new IllegalArgumentException("Branch ID is null");
        }
        try {
            final BranchDto updatedBankDetails = this.service.updateBranch(branchId, branchDto);
            log.info("Branch updated successfully with ID: {}", branchId);
            return updatedBankDetails;
        } catch (Exception e) {
            log.error("Failed to update branch: ", e);
            throw e;
        }
    }

    @KafkaListener(topics = {"${spring.kafka.topics.branch.delete.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "branchKafkaListenerContainerFactory")
    public void deletingBranchListening(BranchDto branchDto) {
        log.info("Received Branch to delete: {}", branchDto.toString());
        final Long branchId = branchDto.getId();
        if (branchId == null) {
            log.warn("Branch ID is null, cannot delete bank details.");
            return;
        }
        this.service.deleteBranchById(branchId);
        log.info("Branch deleted successfully with ID: {}", branchId);
    }

    @KafkaListener(topics = {"${spring.kafka.topics.branch.get.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "branchKafkaListenerContainerFactory")
    public void gettingBranchListening(BranchDto branchDto) throws ValidationException {
        log.info("Received bankDetailsDto to get: {}", branchDto);
        if (branchDto != null) {
            final Long branchId = branchDto.getId();
            if (branchId == null) {
                log.warn("Bank ID is null, cannot get branch.");
                return;
            }
            try {
                final BranchDto branchDtoToGet = this.service.getBranchById(branchId);
                log.info("Branch retrieved successfully: {}", branchDtoToGet.toString());
            } catch (Exception e) {
                log.error("Error retrieving branch for ID {}: ", branchId, e);
            }
        } else {
            log.error("Invalid branch received: null");
            throw new ValidationException("Invalid branch received");
        }
    }
}
