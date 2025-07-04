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

    @KafkaListener(topics = "#{kafkaTopic.topicAccountDetailsCreate}", groupId = "#{kafkaTopic.groupId}")
    public void create(AccountDetailsDto dto) {
        accountDetailsService.create(dto);
    }

    @KafkaListener(topics = "#{kafkaTopic.topicAccountDetailsUpdate}", groupId = "#{kafkaTopic.groupId}")
    public void update(Long id, AccountDetailsDto dto) {
        accountDetailsService.update(id, dto);
    }

    @KafkaListener(topics = "#{kafkaTopic.topicAccountDetailsDelete}", groupId = "#{kafkaTopic.groupId}")
    public void delete(Long id) {
        accountDetailsService.delete(id);
    }

    @KafkaListener(topics = "#{kafkaTopic.topicAccountDetailsGet}", groupId = "#{kafkaTopic.groupId}")
    public void get(Long id) {
        accountDetailsProducer.sendGetResponse(accountDetailsService.get(id));
    }
}
