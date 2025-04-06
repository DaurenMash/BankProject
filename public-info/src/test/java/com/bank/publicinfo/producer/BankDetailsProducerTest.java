package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.BankDetailsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankDetailsProducerTest {

    @Mock
    private KafkaTemplate<String, BankDetailsDto> kafkaTemplate;

    @InjectMocks
    private BankDetailsProducer producer;

    @Test
    void testSendBankDetailsToKafka_Success() {
        BankDetailsDto bankDetailsDto = BankDetailsDto.builder().id(1L).build();
        String topic = "testTopic";
        producer.sendBankDetails(topic, bankDetailsDto);
        verify(kafkaTemplate, times(1)).send(topic, bankDetailsDto);
    }

    @Test
    void testSendBankDetailsToKafka_NullDto() {
        String topic = "testTopic";
        producer.sendBankDetails(topic, null);
        verify(kafkaTemplate, times(1)).send(topic, null);
    }

    @Test
    void testSendMessage_KafkaThrowsException() {
        String topic = "testTopic";
        BankDetailsDto bankDetailsDto = BankDetailsDto.builder().id(1L).build();
        doThrow(new RuntimeException("Kafka Exception")).when(kafkaTemplate).send(topic, bankDetailsDto);
        assertThrows(RuntimeException.class, () -> producer.sendBankDetails(topic, bankDetailsDto));
        verify(kafkaTemplate, times(1)).send(topic, bankDetailsDto);
    }

}
