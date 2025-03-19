package com.bank.history.service;

import com.bank.history.entity.History;
import com.bank.history.repository.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryServiceImpl(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    @Transactional
    public void saveHistory(History history) {

        historyRepository.save(history);

        log.info("History saved: {}", history.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<History> getAuditHistory(Pageable pageable) {
        return historyRepository.findAll(pageable);
    }
}
