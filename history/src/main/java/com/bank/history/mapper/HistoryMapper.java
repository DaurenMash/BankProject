package com.bank.history.mapper;

import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    HistoryDto toDto(History history);

    History toEntity(HistoryDto historyDto);

}
