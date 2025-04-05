package com.bank.publicinfo.mapper;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.entity.Branch;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface BranchMapper {

    BranchDto toDto(Branch branch);

    Branch toEntity(BranchDto branchDto);

    @Mapping(target = "atms", ignore = true)
    void updateFromDto(BranchDto branchDto, @MappingTarget Branch branch);

}
