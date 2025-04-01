package service;

import com.bank.history.entity.History;
import com.bank.history.repository.HistoryRepository;
import com.bank.history.service.HistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HistoryServiceImplTest {

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private HistoryServiceImpl historyService;

    private History history;

    @BeforeEach
    void setUp() {
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
    void testSaveHistory_Success() {
        when(historyRepository.save(history)).thenReturn(history);

        historyService.saveHistory(history);

        verify(historyRepository, times(1)).save(history);
    }

    @Test
    void testSaveHistory_RepositoryThrowsException() {
        when(historyRepository.save(history)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> historyService.saveHistory(history));
        verify(historyRepository, times(1)).save(history);
    }

    @Test
    void testGetAuditHistory_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<History> historyList = List.of(history);
        Page<History> historyPage = new PageImpl<>(historyList, pageable, 1);
        when(historyRepository.findAll(pageable)).thenReturn(historyPage);

        Page<History> result = historyService.getAuditHistory(pageable);

        assertEquals(historyPage, result);
        assertEquals(1, result.getTotalElements());
        assertEquals(historyList, result.getContent());
        verify(historyRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAuditHistory_EmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<History> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(historyRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<History> result = historyService.getAuditHistory(pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(historyRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAuditHistory_RepositoryThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(historyRepository.findAll(pageable)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> historyService.getAuditHistory(pageable));
        verify(historyRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAuditHistoryByTransferId_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<History> historyList = List.of(history);
        Page<History> historyPage = new PageImpl<>(historyList, pageable, 1);
        when(historyRepository.findByTransferAuditId(2L, pageable)).thenReturn(historyPage);

        Page<History> result = historyService.getAuditHistoryByTransferId(2L, pageable);

        assertEquals(historyPage, result);
        assertEquals(1, result.getTotalElements());
        assertEquals(historyList, result.getContent());
        verify(historyRepository, times(1)).findByTransferAuditId(2L, pageable);
    }

    @Test
    void testGetAuditHistoryByTransferId_EmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<History> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(historyRepository.findByTransferAuditId(2L, pageable)).thenReturn(emptyPage);

        Page<History> result = historyService.getAuditHistoryByTransferId(2L, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(historyRepository, times(1)).findByTransferAuditId(2L, pageable);
    }

    @Test
    void testGetAuditHistoryByTransferId_RepositoryThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(historyRepository.findByTransferAuditId(2L, pageable)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> historyService.getAuditHistoryByTransferId(2L, pageable));
        verify(historyRepository, times(1)).findByTransferAuditId(2L, pageable);
    }
}
