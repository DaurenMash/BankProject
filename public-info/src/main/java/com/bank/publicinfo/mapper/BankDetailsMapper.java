package com.bank.publicinfo.mapper;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.entity.BankDetails;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface BankDetailsMapper {

    BankDetailsDto toDto(BankDetails bankDetails);

    BankDetails toEntity(BankDetailsDto bankDetailsDto);

    void updateFromDto(BankDetailsDto bankDetailsDto, @MappingTarget BankDetails bankDetails);

}
