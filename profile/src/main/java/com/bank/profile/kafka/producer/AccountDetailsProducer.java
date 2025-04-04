package com.bank.profile.kafka.producer;

import com.bank.profile.config.KafkaTopicsConfig;
import com.bank.profile.dto.AccountDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountDetailsProducer {
    private final KafkaTopicsConfig topicsConfig;
    private final KafkaTemplate<String, AccountDetailsDto> kafkaTemplateAccount;
    private final KafkaTemplate<String, Long> kafkaTemplateById;

    public void sendCreate(AccountDetailsDto dto) {
        kafkaTemplateAccount.send(topicsConfig.getTopicAccountDetailsCreate(), dto);
    }

    public void sendUpdate(AccountDetailsDto dto) {
        kafkaTemplateAccount.send(topicsConfig.getTopicAccountDetailsUpdate(), dto);
    }

    public void sendDelete(Long id) {
        kafkaTemplateById.send(topicsConfig.getTopicAccountDetailsDelete(), id);
    }

    public void sendGet(Long id) {
        kafkaTemplateById.send(topicsConfig.getTopicAccountDetailsGet(), id);
    }

    public void sendGetResponse(AccountDetailsDto dto) {
        kafkaTemplateAccount.send(topicsConfig.getTopicAccountDetailsGetResponse(), dto);
    }
}
