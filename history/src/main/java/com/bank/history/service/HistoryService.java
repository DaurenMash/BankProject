package com.bank.history.service;

import com.bank.history.dto.HistoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HistoryService {
    List<HistoryDto> getAllHistory();

    HistoryDto getHistoryById(Long id);

    HistoryDto getAggregatedHistory();

    HistoryDto getHistoryByMicroservice(String microservice);
}
