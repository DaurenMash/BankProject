package com.bank.history.controller;

import com.bank.history.dto.HistoryDto;
import com.bank.history.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping()
public class HistoryController {

    private final HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/")
    public ResponseEntity<List<HistoryDto>> getAllHistory() {
        return ResponseEntity.ok(historyService.getAllHistory());
    }

    @GetMapping("/aggregate")
    public ResponseEntity<HistoryDto> getAggregatedHistory() {
        return ResponseEntity.ok(historyService.getAggregatedHistory());
    }

    @GetMapping("/{microservice}")
    public ResponseEntity<HistoryDto> getHistoryByMicroservice(@PathVariable String microservice) {
        return ResponseEntity.ok(historyService.getHistoryByMicroservice(microservice));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<HistoryDto> getHistoryById(@PathVariable Long id) {
        final HistoryDto historyDto = historyService.getHistoryById(id);
        return ResponseEntity.ok(historyDto);
    }
}
