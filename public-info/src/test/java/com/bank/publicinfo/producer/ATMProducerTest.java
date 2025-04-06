package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.ATMDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ATMProducerTest {

    @Mock
    private KafkaTemplate<String, ATMDto> kafkaTemplate;

    @InjectMocks
    private ATMProducer producer;

    @Test
    void sendATMToKafkaTest() {
        ATMDto atmDto = ATMDto.builder().id(1L).build();
        String topic = "ATMTopicTest";
        producer.sendATM(topic, atmDto);
        verify(kafkaTemplate, times(1)).send(topic, atmDto);
    }

}
