package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.ATMDto;
import com.bank.publicinfo.service.ATMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ATMConsumerTest {

    @Mock
    private ATMService atmService;

    @InjectMocks
    private ATMConsumer atmConsumer;

    private ATMDto validAtmDto;
    private ATMDto nullIdAtmDto;

    @BeforeEach
    void setUp() {
        validAtmDto = new ATMDto();
        validAtmDto.setId(1L);
        validAtmDto.setAddress("Test Address");

        nullIdAtmDto = new ATMDto();
        nullIdAtmDto.setId(null);
        nullIdAtmDto.setAddress("Test Address");
    }

    @Test
    void testCreatingATMListening_ShouldCreateAndLogSuccess_WhenValidInput() {
        ATMDto savedAtmDto = new ATMDto();
        savedAtmDto.setId(1L);
        when(atmService.createNewATM(validAtmDto)).thenReturn(savedAtmDto);
        ATMDto result = atmConsumer.creatingATMListening(validAtmDto);
        assertEquals(savedAtmDto, result);
    }

    @Test
    void testCreatingATMListening_ShouldLogError_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Service error");
        when(atmService.createNewATM(validAtmDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () -> atmConsumer.creatingATMListening(validAtmDto));
    }

    @Test
    void testUpdatingATMListening_ShouldUpdateAndLogSuccess_WhenValidInput() {
        when(atmService.updateATM(validAtmDto)).thenReturn(validAtmDto);
        ATMDto result = atmConsumer.updatingATMListening(validAtmDto);
        assertEquals(validAtmDto, result);
    }

    @Test
    void testUpdatingATMListening_ShouldThrowExceptionAndLog_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> atmConsumer.updatingATMListening(nullIdAtmDto)
        );
        assertEquals("ATM id is null, cannot update ATM.", exception.getMessage());
    }

    @Test
    void testUpdatingATMListening_ShouldLogError_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Update error");
        when(atmService.updateATM(validAtmDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () -> atmConsumer.updatingATMListening(validAtmDto));
    }

    @Test
    void testDeletingATMListening_ShouldDeleteAndLogSuccess_WhenValidInput() {
        atmConsumer.deletingATMListening(validAtmDto);
        verify(atmService).deleteATMById(1L);
    }

    @Test
    void testDeletingATMListening_ShouldLogWarning_WhenIdIsNull() {
        atmConsumer.deletingATMListening(nullIdAtmDto);
    }

    @Test
    void testGettingATMListening_ShouldGetAndLogSuccess_WhenValidInput() {
        when(atmService.getATMById(1L)).thenReturn(validAtmDto);
        atmConsumer.gettingATMListening(validAtmDto);
    }

    @Test
    void testGettingATMListening_ShouldLogWarning_WhenIdIsNull() {
        atmConsumer.gettingATMListening(nullIdAtmDto);
    }

    @Test
    void testGettingATMListening_ShouldLogError_WhenInputIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                atmConsumer.gettingATMListening(null));
    }

    @Test
    void testGettingATMListening_ShouldLogError_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Get error");
        when(atmService.getATMById(1L)).thenThrow(exception);
        atmConsumer.gettingATMListening(validAtmDto);
    }
}
