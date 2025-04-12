package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.BankDetailsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankDetailsProducer {

    private final KafkaTemplate<String, BankDetailsDto> kafkaTemplate;

    public void sendBankDetails(String topic, BankDetailsDto bankDetailsDto) {
        kafkaTemplate.send(topic, bankDetailsDto);
        log.info("Bank details sent to topic: {}", topic);
    }

}
