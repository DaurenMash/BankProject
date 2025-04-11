package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
@Slf4j
public class ErrorLogsConsumer {

    @KafkaListener(topics = {"${spring.kafka.topics.error-log.name}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "errorLogsKafkaListenerContainerFactory")
    public void listen(ErrorResponseDto errorResponse) {
        log.info("Received error log: Code: {}, Message: {}, Timing: {}", errorResponse
                .getErrorCode(), errorResponse
                .getMessage(), errorResponse
                .getTimestamp());
    }
}
