package com.bank.history.handlers;


import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryKafkaListener {

    private final HistoryService historyService;
    private final HistoryMapper historyMapper;
    private final KafkaTemplate<String, HistoryDto> kafkaTemplate;

    @KafkaListener(topics = "audit.history", groupId = "history-group")
    public void listenAuditHistory(@Header(value = KafkaHeaders.KEY, required = false) String key,
                                   @Payload HistoryDto historyDto) {
        try {
            final History history = historyMapper.toEntity(historyDto);
            historyService.saveHistory(history);
        } catch (Exception e) {
            log.error("Error processing audit.history message [key: {}]: {}", key, historyDto, e);
        }
    }

    @KafkaListener(topics = "audit.history.request", groupId = "history-group")
    public void listenHistoryRequest(@Header(value = KafkaHeaders.KEY, required = false) String key,
                                     @Payload HistoryDto historyDto) {
        try {
            if (historyDto.getTransferAuditId() == null) {
                log.warn("Invalid history request - missing transferAuditId [key: {}]", key, historyDto);
                return;
            }

            final List<History> allHistory = new ArrayList<>();
            int page = 0;
            final int pageSize = 100;
            Page<History> historyPage;

            do {
                final Pageable pageable = PageRequest.of(page, pageSize);
                historyPage = historyService.getAuditHistoryByTransferId(historyDto.getTransferAuditId(), pageable);
                allHistory.addAll(historyPage.getContent());
                page++;
            } while (historyPage.hasNext());

            final List<HistoryDto> historyDtoList = allHistory.stream().map(historyMapper::toDto).toList();

            for (HistoryDto dto : historyDtoList) {
                kafkaTemplate.send("audit.history.response", dto.getTransferAuditId().toString(), dto);
            }
        } catch (Exception e) {
            log.error("Error processing history request [key: {}]: {}", key, historyDto, e);
        }
    }
}
