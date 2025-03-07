package com.bank.history.mapper;

import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "transferAuditId", target = "transferAuditId"),
            @Mapping(source = "profileAuditId", target = "profileAuditId"),
            @Mapping(source = "accountAuditId", target = "accountAuditId"),
            @Mapping(source = "antiFraudAuditId", target = "antiFraudAuditId"),
            @Mapping(source = "publicBankInfoAuditId", target = "publicBankInfoAuditId"),
            @Mapping(source = "authorizationAuditId", target = "authorizationAuditId")
    })
    HistoryDto toDto(History history);

    @InheritInverseConfiguration
    History toEntity(HistoryDto historyDto);

}
