package com.bank.profile.kafka.producer;

import com.bank.profile.config.KafkaTopicsConfig;
import com.bank.profile.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileProducer {
    private final KafkaTopicsConfig topicsConfig;
    private final KafkaTemplate<String, ProfileDto> kafkaTemplateProfile;
    private final KafkaTemplate<String, Long> kafkaTemplateById;

    public void sendUpdate(ProfileDto dto) {
        kafkaTemplateProfile.send(topicsConfig.getTopicProfileUpdate(), dto);
    }

    public void sendDelete(Long id) {
        kafkaTemplateById.send(topicsConfig.getTopicProfileDelete(), id);
    }

    public void sendGet(Long id) {
        kafkaTemplateById.send(topicsConfig.getTopicProfileGet(), id);
    }

    public void sendGetResponse(ProfileDto dto) {
        kafkaTemplateProfile.send(topicsConfig.getTopicProfileGetResponse(), dto);
    }
}
