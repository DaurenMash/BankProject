package com.bank.history.service;

import com.bank.history.entity.History;
import com.bank.history.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryServiceImpl(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public History getAggregatedHistory() {
        return historyRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Нет данных в истории"));
    }

    @Override
    public History getHistoryByMicroservice(String microservice) {

        return historyRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Нет данных для микросервиса: " + microservice));
    }
}

