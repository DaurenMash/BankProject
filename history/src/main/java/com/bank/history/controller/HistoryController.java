package com.bank.history.controller;

import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class HistoryController {

    private final HistoryService historyService;
    private final HistoryMapper historyMapper;

    @Autowired
    public HistoryController(HistoryService historyService, HistoryMapper historyMapper) {
        this.historyService = historyService;
        this.historyMapper = historyMapper;
    }

    @GetMapping("/aggregate")
    public ResponseEntity<HistoryDto> getAggregatedHistory() {
        History aggregatedHistory = historyService.getAggregatedHistory();
        HistoryDto dto = historyMapper.toDto(aggregatedHistory);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{microservice}")
    public ResponseEntity<HistoryDto> getHistoryByMicroservice(@PathVariable String microservice) {
        History history = historyService.getHistoryByMicroservice(microservice);
        HistoryDto dto = historyMapper.toDto(history);
        return ResponseEntity.ok(dto);
    }
}
