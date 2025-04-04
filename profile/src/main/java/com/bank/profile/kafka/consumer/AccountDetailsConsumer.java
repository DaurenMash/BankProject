package com.bank.profile.kafka.consumer;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.kafka.producer.AccountDetailsProducer;
import com.bank.profile.service.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountDetailsConsumer {
    private final AccountDetailsService accountDetailsService;
    private final AccountDetailsProducer accountDetailsProducer;

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicAccountDetailsCreate}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void create(AccountDetailsDto dto) {
        accountDetailsService.create(dto);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicAccountDetailsUpdate}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void update(AccountDetailsDto dto) {
        accountDetailsService.update(dto);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicAccountDetailsDelete}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void delete(Long id) {
        accountDetailsService.delete(id);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicAccountDetailsGet}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void get(Long id) {
        accountDetailsProducer.sendGetResponse(accountDetailsService.get(id));
    }
}
