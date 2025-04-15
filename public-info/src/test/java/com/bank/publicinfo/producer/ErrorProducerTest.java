package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ErrorProducerTest {

    @Mock
    private KafkaTemplate<String, ErrorResponseDto> kafkaTemplate;

    @InjectMocks
    private ErrorProducer producer;

    @Test
    void testSendErrorToKafka() {
        ErrorResponseDto errorResponseDto =
                new ErrorResponseDto("testErrorCode","testMessage");
        String topic = "testErrorTopic";
        producer.sendErrorLog(topic,errorResponseDto);
        verify(kafkaTemplate, times(1)).send(topic,errorResponseDto);
    }

}
