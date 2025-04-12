package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.BranchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchProducer {

    private final KafkaTemplate<String, BranchDto> kafkaTemplate;

    public void sendBranch(String topic, BranchDto branchDto) {
        kafkaTemplate.send(topic, branchDto);
        log.info("Branch sent to topic {}", topic);
    }

}
