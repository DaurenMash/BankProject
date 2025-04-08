package handlers;

import com.bank.history.dto.HistoryDto;
import com.bank.history.handlers.HistoryKafkaProducer;
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
public class HistoryKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, HistoryDto> kafkaTemplate;

    @InjectMocks
    private HistoryKafkaProducer kafkaProducer;

    @Test
    void testSendMessage_Success() {
        HistoryDto historyDto = HistoryDto.builder().id(1L).build();
        String topic = "test-topic";

        kafkaProducer.sendMessage(topic, historyDto);

        verify(kafkaTemplate, times(1)).send(topic, historyDto);
    }

    @Test
    void testSendMessage_NullDto() {
        String topic = "test-topic";

        kafkaProducer.sendMessage(topic, null);

        verify(kafkaTemplate, times(1)).send(topic, null);
    }

    @Test
    void testSendMessage_KafkaThrowsException() {
        HistoryDto historyDto = HistoryDto.builder().id(1L).build();
        String topic = "test-topic";
        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(topic, historyDto);

        assertThrows(RuntimeException.class, () -> kafkaProducer.sendMessage(topic, historyDto));
        verify(kafkaTemplate, times(1)).send(topic, historyDto);
    }
}
