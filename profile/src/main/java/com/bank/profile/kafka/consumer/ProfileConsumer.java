package com.bank.profile.kafka.consumer;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.kafka.producer.ProfileProducer;
import com.bank.profile.service.ProfileService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProfileConsumer {
    private final ProfileService profileService;
    private final ProfileProducer profileProducer;

    public ProfileConsumer(ProfileService profileService, ProfileProducer profileProducer) {
        this.profileService = profileService;
        this.profileProducer = profileProducer;
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicProfileCreate}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void create(ProfileDto dto) {
        profileService.create(dto);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicProfileUpdate}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void update(ProfileDto dto) {
        profileService.update(dto);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicProfileDelete}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void delete(Long id) {
        profileService.delete(id);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.topicProfileGet}", groupId = "#{kafkaTopicsConfig.groupId}")
    public void get(Long id) {
        profileProducer.sendGetResponse(profileService.get(id));
    }
}
