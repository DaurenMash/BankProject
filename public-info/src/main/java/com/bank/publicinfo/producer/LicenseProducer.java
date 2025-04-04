package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.LicenseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenseProducer {

    private final KafkaTemplate<String, LicenseDto> kafkaTemplate;

    public void sendLicense(String topic, LicenseDto licenseDto) {
        kafkaTemplate.send(topic, licenseDto);
        log.info("License sent to topic: {}", topic);
    }

}
