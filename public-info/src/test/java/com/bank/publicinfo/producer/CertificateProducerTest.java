package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.CertificateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CertificateProducerTest {

    @Mock
    private KafkaTemplate<String, CertificateDto> kafkaTemplate;

    @InjectMocks
    private CertificateProducer producer;

    @Test
    public void testSendCertificateToKafka() {
        CertificateDto certificateDto = CertificateDto.builder().id(1L).build();
        String topic = "testTopic";
        producer.sendCertificate(topic, certificateDto);
        verify(kafkaTemplate, times(1)).send(topic, certificateDto);
    }

}
