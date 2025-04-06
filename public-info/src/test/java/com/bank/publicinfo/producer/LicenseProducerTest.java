package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.LicenseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LicenseProducerTest {

    @Mock
    private KafkaTemplate<String, LicenseDto> kafkaTemplate;

    @InjectMocks
    private LicenseProducer producer;

    @Test
    void sendLicenseToKafkaTest() {
        String topic = "licenseTopicTest";
        LicenseDto licenseDto = LicenseDto.builder().id(1L).build();
        producer.sendLicense(topic, licenseDto);
        verify(kafkaTemplate, times(1)).send(topic, licenseDto);
    }

}
