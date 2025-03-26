package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.model.SuspiciousAccountTransfer;
import com.bank.antifraud.model.SuspiciousCardTransfer;
import com.bank.antifraud.model.SuspiciousPhoneTransfer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface SuspiciousTransferMapper {
    SuspiciousAccountTransferDto toDTO(SuspiciousAccountTransfer accountTransfer);
    SuspiciousPhoneTransferDto toDTO(SuspiciousPhoneTransfer accountTransfer);
    SuspiciousCardTransferDto toDTO(SuspiciousCardTransfer cardTransfer);

    SuspiciousCardTransfer toEntity(SuspiciousCardTransferDto cardTransferDto);
    SuspiciousPhoneTransfer toEntity(SuspiciousPhoneTransferDto phoneTransferDto);
    SuspiciousAccountTransfer toEntity(SuspiciousAccountTransferDto accountTransferDto);
}
