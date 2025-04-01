package handlers;

import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.handlers.HistoryKafkaListener;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryKafkaListenerTest {

    @Mock
    private HistoryService historyService;

    @Mock
    private HistoryMapper historyMapper;

    @Mock
    private KafkaTemplate<String, HistoryDto> kafkaTemplate;

    @InjectMocks
    private HistoryKafkaListener kafkaListener;

    private HistoryDto historyDto;
    private History history;

    @BeforeEach
    void setUp() {
        historyDto = HistoryDto.builder()
                .id(1L)
                .transferAuditId(2L)
                .profileAuditId(3L)
                .accountAuditId(4L)
                .antiFraudAuditId(5L)
                .publicBankInfoAuditId(6L)
                .authorizationAuditId(7L)
                .build();

        history = History.builder()
                .id(1L)
                .transferAuditId(2L)
                .profileAuditId(3L)
                .accountAuditId(4L)
                .antiFraudAuditId(5L)
                .publicBankInfoAuditId(6L)
                .authorizationAuditId(7L)
                .build();
    }

    @Test
    void testListenAuditHistory_Success() {
        when(historyMapper.toEntity(historyDto)).thenReturn(history);

        kafkaListener.listenAuditHistory("key", historyDto);

        verify(historyService, times(1)).saveHistory(history);
    }

    @Test
    void testListenAuditHistory_NullDto() {
        kafkaListener.listenAuditHistory("key", null);

        verify(historyMapper, never()).toEntity(any());
        verify(historyService, never()).saveHistory(any());
    }

    @Test
    void testListenAuditHistory_MapperThrowsException() {
        when(historyMapper.toEntity(historyDto)).thenThrow(new RuntimeException("Mapper error"));

        kafkaListener.listenAuditHistory("key", historyDto);

        verify(historyService, never()).saveHistory(any());
    }

    @Test
    void testListenAuditHistory_ServiceThrowsException() {
        when(historyMapper.toEntity(historyDto)).thenReturn(history);
        doThrow(new RuntimeException("Service error")).when(historyService).saveHistory(history);

        kafkaListener.listenAuditHistory("key", historyDto);

        verify(historyService, times(1)).saveHistory(history);
    }

    @Test
    void testListenHistoryRequest_Success() {
        Pageable pageable = PageRequest.of(0, 100);
        List<History> historyList = List.of(history);
        Page<History> historyPage = mock(Page.class);

        when(historyService.getAuditHistoryByTransferId(2L, pageable)).thenReturn(historyPage);
        when(historyPage.hasNext()).thenReturn(false);
        when(historyPage.getContent()).thenReturn(historyList);
        when(historyMapper.toDto(history)).thenReturn(historyDto);

        kafkaListener.listenHistoryRequest("key", historyDto);

        verify(kafkaTemplate, times(1)).send("audit.history.response", "2", historyDto);
    }

    @Test
    void testListenHistoryRequest_NullDto() {
        kafkaListener.listenHistoryRequest("key", null);

        verify(historyService, never()).getAuditHistoryByTransferId(anyLong(), any(Pageable.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(HistoryDto.class));
    }

    @Test
    void testListenHistoryRequest_NullTransferAuditId() {
        HistoryDto invalidDto = HistoryDto.builder().id(1L).build();

        kafkaListener.listenHistoryRequest("key", invalidDto);

        verify(historyService, never()).getAuditHistoryByTransferId(anyLong(), any(Pageable.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(HistoryDto.class));
    }

    @Test
    void testListenHistoryRequest_EmptyHistoryPage() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<History> emptyPage = mock(Page.class);

        when(historyService.getAuditHistoryByTransferId(2L, pageable)).thenReturn(emptyPage);
        when(emptyPage.hasNext()).thenReturn(false);
        when(emptyPage.getContent()).thenReturn(Collections.emptyList());

        kafkaListener.listenHistoryRequest("key", historyDto);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(HistoryDto.class));
    }

    @Test
    void testListenHistoryRequest_MultiPage_Success() {
        Pageable pageable1 = PageRequest.of(0, 100);
        Pageable pageable2 = PageRequest.of(1, 100);
        List<History> historyList1 = List.of(history);
        List<History> historyList2 = List.of(history);
        Page<History> historyPage1 = mock(Page.class);
        Page<History> historyPage2 = mock(Page.class);

        when(historyService.getAuditHistoryByTransferId(2L, pageable1)).thenReturn(historyPage1);
        when(historyPage1.hasNext()).thenReturn(true);
        when(historyPage1.getContent()).thenReturn(historyList1);
        when(historyService.getAuditHistoryByTransferId(2L, pageable2)).thenReturn(historyPage2);
        when(historyPage2.hasNext()).thenReturn(false);
        when(historyPage2.getContent()).thenReturn(historyList2);
        when(historyMapper.toDto(history)).thenReturn(historyDto);

        kafkaListener.listenHistoryRequest("key", historyDto);

        verify(kafkaTemplate, times(2)).send("audit.history.response", "2", historyDto);
    }

    @Test
    void testListenHistoryRequest_ServiceThrowsException() {
        Pageable pageable = PageRequest.of(0, 100);
        when(historyService.getAuditHistoryByTransferId(2L, pageable)).thenThrow(new RuntimeException("Service error"));

        kafkaListener.listenHistoryRequest("key", historyDto);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(HistoryDto.class));
    }

    @Test
    void testListenHistoryRequest_MapperThrowsException() {
        Pageable pageable = PageRequest.of(0, 100);
        List<History> historyList = List.of(history);
        Page<History> historyPage = mock(Page.class);

        when(historyService.getAuditHistoryByTransferId(2L, pageable)).thenReturn(historyPage);
        when(historyPage.hasNext()).thenReturn(false);
        when(historyPage.getContent()).thenReturn(historyList);
        when(historyMapper.toDto(history)).thenThrow(new RuntimeException("Mapper error"));

        kafkaListener.listenHistoryRequest("key", historyDto);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(HistoryDto.class));
    }
}
