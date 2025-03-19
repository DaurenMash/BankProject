package com.bank.history.service;

import com.bank.history.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


public interface HistoryService {

    void saveHistory(History history);

    @Transactional(readOnly = true)
    Page<History> getAuditHistory(Pageable pageable);
}
