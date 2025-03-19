package com.bank.history.service;

import com.bank.history.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface HistoryService {

    void saveHistory(History history);

    Page<History> getAuditHistory(Pageable pageable);
}
