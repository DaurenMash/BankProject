package com.bank.profile.kafka.consumer;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.kafka.producer.ProfileProducer;
import com.bank.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileConsumer {
    private final ProfileService profileService;
    private final ProfileProducer profileProducer;

    @KafkaListener(topics = "#{kafkaTopic.topicProfileCreate}", groupId = "#{kafkaTopic.groupId}")
    public void create(ProfileDto dto) {
        profileService.create(dto);
    }

    @KafkaListener(topics = "#{kafkaTopic.topicProfileUpdate}", groupId = "#{kafkaTopic.groupId}")
    public void update(Long id, ProfileDto dto) {
        profileService.update(id, dto);
    }

    @KafkaListener(topics = "#{kafkaTopic.topicProfileDelete}", groupId = "#{kafkaTopic.groupId}")
    public void delete(Long id) {
        profileService.delete(id);
    }

    @KafkaListener(topics = "#{kafkaTopic.topicProfileGet}", groupId = "#{kafkaTopic.groupId}")
    public void get(Long id) {
        profileProducer.sendGetResponse(profileService.get(id));
    }
}
