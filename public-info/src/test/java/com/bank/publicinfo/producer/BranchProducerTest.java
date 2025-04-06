package com.bank.publicinfo.producer;

import com.bank.publicinfo.dto.BranchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BranchProducerTest {

    @Mock
    private KafkaTemplate<String, BranchDto> kafkaTemplate;

    @InjectMocks
    private BranchProducer producer;

    @Test
    void sendBranchToKafkaTest() {
        BranchDto branchDto = BranchDto.builder().id(1L).build();
        String topic = "branchTopicTest";
        producer.sendBranch(topic, branchDto);
        verify(kafkaTemplate, times(1)).send(topic, branchDto);
    }

}
