package com.bank.history.service;

import com.bank.history.entity.History;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HistoryService {

    void saveHistory(History history);

    List<History> getAuditHistory();

}
