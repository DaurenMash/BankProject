package com.bank.history.service;

import com.bank.history.entity.History;
import com.bank.history.repository.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryServiceImpl(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public void saveHistory(History history) {

        log.info("Save history");

        historyRepository.save(history);

        log.info("History saved");
    }

    @Override
    public List<History> getAuditHistory() {
        return historyRepository.findAll();
    }
}
