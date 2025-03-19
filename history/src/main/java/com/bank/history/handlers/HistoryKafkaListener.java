package com.bank.history.handlers;


import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.domain.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class HistoryKafkaListener {

    private final HistoryService historyService;
    private final HistoryMapper historyMapper;
    private final KafkaTemplate<String, HistoryDto> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public HistoryKafkaListener(HistoryService historyService,
                                HistoryMapper historyMapper,
                                KafkaTemplate<String, HistoryDto> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.historyService = historyService;
        this.historyMapper = historyMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "audit.history", groupId = "history-group")
    public void listenAuditHistory(@Payload HistoryDto historyDto) {
        try {
            log.info("Получено сообщение из audit.history: {}", historyDto);
            if (historyDto == null) {
                log.warn("Сообщение пустое");
                return;
            }
            log.info("Преобразование HistoryDto в History: {}", historyDto);
            History history = historyMapper.toEntity(historyDto);
            log.info("Сущность History перед сохранением: id={}, transferAuditId={}, ...",
                    history.getId(), history.getTransferAuditId());
            historyService.saveHistory(history);
            log.info("Сохранение успешно выполнено для History: {}", history);
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения из audit.history: {}", historyDto, e);
        }
    }

    @KafkaListener(topics = "audit.history.request", groupId = "history-group")
    public void listenHistoryRequest(@Payload HistoryDto historyDto) {
        try {
            log.info("Получен запрос на историю изменений: {}", historyDto);

            if (historyDto == null || historyDto.getTransferAuditId() == null) {
                log.warn("Некорректный запрос истории: {}", historyDto);
                return;
            }

            List<History> allHistory = new ArrayList<>();
            int page = 0;
            int pageSize = 100;
            Page<History> historyPage;

            do {
                Pageable pageable = PageRequest.of(page, pageSize);
                historyPage = historyService.getAuditHistory(pageable);
                allHistory.addAll(historyPage.getContent());
                page++;
            } while (historyPage.hasNext());

            log.info("Получено {} записей истории", allHistory.size());
            final List<HistoryDto> historyDtoList = allHistory.stream()
                    .map(historyMapper::toDto)
                    .toList();

            for (HistoryDto dto : historyDtoList) {
                kafkaTemplate.send("audit.history.response", dto);
                log.info("Отправлен ответ: {}", dto);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке запроса истории: {}", historyDto, e);
        }
    }
}
