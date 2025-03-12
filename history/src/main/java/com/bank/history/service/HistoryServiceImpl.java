package com.bank.history.service;

import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.repository.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;
    private final KafkaTemplate<String, HistoryDto> kafkaTemplate;

    public HistoryServiceImpl(HistoryRepository historyRepository,
                              HistoryMapper historyMapper,
                              KafkaTemplate<String, HistoryDto> kafkaTemplate) {
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.kafkaTemplate = kafkaTemplate;
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
