package com.bank.history.handlers;


import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

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
    public void listenAuditHistory(@Payload String jsonMessage) {
        try {
            log.info("Получено сообщение из audit.history: {}", jsonMessage);
            HistoryDto historyDto = objectMapper.readValue(jsonMessage, HistoryDto.class);
            if (historyDto == null) {
                log.warn("Не удалось десериализовать сообщение в HistoryDto: {}", jsonMessage);
                return;
            }
            log.info("Преобразование HistoryDto в History: {}", historyDto);
            History history = historyMapper.toEntity(historyDto);
            log.info("Сущность History перед сохранением: id={}, transferAuditId={}, ...",
                    history.getId(), history.getTransferAuditId());
            historyService.saveHistory(history);
            log.info("Сохранение успешно выполнено для History: {}", history);
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения из audit.history: {}", jsonMessage, e);
        }
    }

    @KafkaListener(topics = "audit.history.request", groupId = "history-group")
    public void listenHistoryRequest(ConsumerRecord<String, String> record) {
        try {
            String request = record.value();
            log.info("Получен запрос на историю изменений: key={}, value={}", record.key(), request);
            if (request == null) {
                log.warn("Запрос истории пустой: key={}", record.key());
                return;
            }
            final List<History> historyList = historyService.getAuditHistory();
            log.info("Получено {} записей истории", historyList.size());
            final List<HistoryDto> historyDtoList = historyList.stream()
                    .map(historyMapper::toDto)
                    .toList();

            for (HistoryDto historyDto : historyDtoList) {
                kafkaTemplate.send("audit.history.response", historyDto);
                log.info("Отправлен ответ: {}", historyDto);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке запроса истории: key={}, value={}", record.key(), record.value(), e);
        }
    }
}