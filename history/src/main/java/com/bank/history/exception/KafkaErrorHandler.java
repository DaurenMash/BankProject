package com.bank.history.exception;

import com.bank.history.dto.HistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaErrorHandler {

    private static final long RETRY_INTERVAL_MS = 1000L;
    private static final long MAX_RETRIES = 3;


    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, HistoryDto> kafkaTemplate) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate, (record, ex) -> {
                    log.error("Sending to DLT: {} due to error: {}", record.value(), ex.getMessage());
                    return new TopicPartition("history-dlt", record.partition());
                }),
                new FixedBackOff(RETRY_INTERVAL_MS, MAX_RETRIES)
        );
    }
}
