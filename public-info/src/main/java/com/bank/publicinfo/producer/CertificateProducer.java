package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.CertificateDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateProducer {

    private final KafkaTemplate<String, CertificateDto> kafkaTemplate;

    public void sendCertificate(String topic, CertificateDto certificateDto) {
        kafkaTemplate.send(topic, certificateDto);
        log.info("Certificate sent to topic: {}", topic);
    }

}
