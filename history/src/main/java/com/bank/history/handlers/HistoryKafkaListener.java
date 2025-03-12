package com.bank.history.handlers;


import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class HistoryKafkaListener {

    private static final Logger LOG = LoggerFactory.getLogger(HistoryKafkaListener.class);

    private final HistoryService historyService;

    private final HistoryMapper historyMapper;

    private final KafkaTemplate<String, HistoryDto> kafkaTemplate;


    public HistoryKafkaListener(HistoryService historyService,
                                HistoryMapper historyMapper,
                                KafkaTemplate<String, HistoryDto> kafkaTemplate) {
        this.historyService = historyService;
        this.historyMapper = historyMapper;
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(topics = "audit.history", groupId = "history-group")
    public void listenAuditHistory(HistoryDto historyDto) {
        LOG.info("Получено сообщение {}", historyDto);

        final History history = historyMapper.toEntity(historyDto);
        historyService.saveHistory(history);
    }

    @KafkaListener(topics = "audit.history.request", groupId = "history-group")
    public void listenHistoryRequest(String request) {
        LOG.info("Получен запрос на историю изменений: {}", request);

        final List<History> historyList = historyService.getAuditHistory();
        final List<HistoryDto> historyDtoList = historyList.stream()
                .map(historyMapper::toDto)
                .toList();

        for (HistoryDto historyDto : historyDtoList) {
            kafkaTemplate.send("audit.history.response", historyDto);
            LOG.info("Отправлен ответ: {}", historyDto);
        }
    }
}
