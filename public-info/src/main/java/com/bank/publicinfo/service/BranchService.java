package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.exception.ValidationException;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface BranchService {

    BranchDto createNewBranch(BranchDto branchDto) throws ValidationException;

    BranchDto updateBranch(Long branchId, BranchDto branchDto);

    void deleteBranchById(Long branchId);

    List<BranchDto> getAllBranches(Pageable pageable);

    BranchDto getBranchById(Long branchId);

}
