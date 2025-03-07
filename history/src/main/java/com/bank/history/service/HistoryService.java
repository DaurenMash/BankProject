package com.bank.history.service;

import com.bank.history.entity.History;
import org.springframework.stereotype.Service;


@Service
public interface HistoryService {

    History getAggregatedHistory();

    History getHistoryByMicroservice(String microservice);
}
