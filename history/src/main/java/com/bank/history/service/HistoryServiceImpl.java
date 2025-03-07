package com.bank.history.service;

import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.repository.HistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;

    @Autowired
    public HistoryServiceImpl(HistoryRepository historyRepository, HistoryMapper historyMapper) {
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
    }

    @Override
    public List<HistoryDto> getAllHistory() {
        return historyRepository.findAll()
                .stream()
                .map(historyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HistoryDto getHistoryById(Long id) {
        return historyRepository.findById(id)
                .map(historyMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("История не найдена с id: " + id));
    }

    @Override
    public HistoryDto getAggregatedHistory() {
        return historyRepository.findAll()
                .stream()
                .findFirst()
                .map(historyMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Нет данных в истории"));
    }

    @Override
    public HistoryDto getHistoryByMicroservice(String microservice) {

        return historyRepository.findAll()
                .stream()
                .findFirst()
                .map(historyMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Нет данных для микросервиса: " + microservice));
    }
}

