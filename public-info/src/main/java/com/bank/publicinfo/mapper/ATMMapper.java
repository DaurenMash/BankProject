package com.bank.publicinfo.mapper;

import com.bank.publicinfo.dto.ATMDto;
import com.bank.publicinfo.entity.ATM;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ATMMapper {

    @Mapping(source = "branch.id", target = "branchId")
    ATMDto toDto(ATM atm);

    @Mappings({
            @Mapping(target = "branch.id", source = "branchId"),
            @Mapping(target = "allHours", source = "allHours")
    })
    ATM toEntity(ATMDto atmDto);

    @Mapping(target = "branch", ignore = true)
    void updateFromDto(ATMDto atmDto, @MappingTarget ATM atm);

}