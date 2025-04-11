package com.bank.profile.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@NoArgsConstructor
public class KafkaTopic {
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${app.kafka.topic.profile.create}")
    private String topicProfileCreate;
    @Value("${app.kafka.topic.profile.update}")
    private String topicProfileUpdate;
    @Value("${app.kafka.topic.profile.delete}")
    private String topicProfileDelete;
    @Value("${app.kafka.topic.profile.get}")
    private String topicProfileGet;
    @Value("${app.kafka.topic.profile.get-response}")
    private String topicProfileGetResponse;
    @Value("${app.kafka.topic.account.create}")
    private String topicAccountDetailsCreate;
    @Value("${app.kafka.topic.account.update}")
    private String topicAccountDetailsUpdate;
    @Value("${app.kafka.topic.account.delete}")
    private String topicAccountDetailsDelete;
    @Value("${app.kafka.topic.account.get}")
    private String topicAccountDetailsGet;
    @Value("${app.kafka.topic.account.get-response}")
    private String topicAccountDetailsGetResponse;
    @Value("${app.kafka.topic.audit}")
    private String topicAudit;
    @Value("${app.kafka.topic.error}")
    private String topicError;
}
