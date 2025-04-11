package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BranchDto;
import org.springframework.data.domain.Pageable;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface BranchService {

    BranchDto createNewBranch(BranchDto branchDto) throws ValidationException;

    BranchDto updateBranch(Long branchId, BranchDto branchDto);

    void deleteBranchById(Long branchId);

    List<BranchDto> getAllBranches(Pageable pageable);

    BranchDto getBranchById(Long branchId);

}
