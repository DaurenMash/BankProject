package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.exception.ValidationException;
import com.bank.publicinfo.service.BankDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankDetailsConsumerTest {

    @Mock
    private BankDetailsService bankDetailsService;

    @InjectMocks
    private BankDetailsConsumer bankDetailsConsumer;

    private BankDetailsDto validBankDetailsDto;
    private BankDetailsDto nullIdBankDetailsDto;

    @BeforeEach
    void setUp() {
        validBankDetailsDto = new BankDetailsDto();
        validBankDetailsDto.setId(1L);
        validBankDetailsDto.setBik(1234L);

        nullIdBankDetailsDto = new BankDetailsDto();
        nullIdBankDetailsDto.setId(null);
        nullIdBankDetailsDto.setBik(1234L);
    }

    @Test
    void testCreatingBankListening_ShouldCreateSuccess_WhenValidInput() throws ValidationException {
        BankDetailsDto savedBankDetailsDto = new BankDetailsDto();
        savedBankDetailsDto.setId(1L);
        when(bankDetailsService.createNewBankDetails(validBankDetailsDto)).thenReturn(savedBankDetailsDto);
        BankDetailsDto result = bankDetailsConsumer.creatingBankListening(validBankDetailsDto);
        assertEquals(savedBankDetailsDto, result);
    }

    @Test
    void testCreatingBankListening_ShouldThrowException_WhenServiceThrowsException() throws ValidationException {
        RuntimeException e = new RuntimeException("Service error");
        when(bankDetailsService.createNewBankDetails(validBankDetailsDto)).thenThrow(e);
        assertThrows(RuntimeException.class, () ->
                bankDetailsConsumer.creatingBankListening(validBankDetailsDto));
    }

    @Test
    void testUpdatingBankListening_ShouldUpdateSuccess_WhenValidInput() throws ValidationException {
        when(bankDetailsService.updateBankDetails(validBankDetailsDto)).thenReturn(validBankDetailsDto);
        BankDetailsDto result = bankDetailsConsumer.updatingBankListening(validBankDetailsDto);
        assertEquals(validBankDetailsDto, result);
    }

    @Test
    void testUpdatingBankListening_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankDetailsConsumer.updatingBankListening(nullIdBankDetailsDto)
        );
        assertEquals("Bank ID is null", exception.getMessage());
    }

    @Test
    void testUpdatingBankListening_ShouldThrowException_WhenServiceThrowsException() throws ValidationException {
        RuntimeException exception = new RuntimeException("Update error");
        when(bankDetailsService.updateBankDetails(validBankDetailsDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () ->
                bankDetailsConsumer.updatingBankListening(validBankDetailsDto));
    }

    @Test
    void testDeletingBankListening_ShouldDeleteSuccess_WhenValidInput() throws ValidationException {
        bankDetailsConsumer.deletingBankListening(validBankDetailsDto);
        verify(bankDetailsService).deleteBankDetailsById(1L);
    }

    @Test
    void testDeletingBankListening_ShouldDoNothing_WhenIdIsNull() throws ValidationException {
        bankDetailsConsumer.deletingBankListening(nullIdBankDetailsDto);
        verifyNoInteractions(bankDetailsService);
    }

    @Test
    void testGettingBankListening_ShouldGetSuccess_WhenValidInput() throws ValidationException {
        when(bankDetailsService.getBankDetailsById(1L)).thenReturn(validBankDetailsDto);
        bankDetailsConsumer.gettingBankListening(validBankDetailsDto);
        verify(bankDetailsService).getBankDetailsById(1L);
    }

    @Test
    void testGettingBankListening_ShouldDoNothing_WhenIdIsNull() throws ValidationException {
        bankDetailsConsumer.gettingBankListening(nullIdBankDetailsDto);
        verifyNoInteractions(bankDetailsService);
    }

    @Test
    void testGettingBankListening_ShouldThrowException_WhenInputIsNull() {
        assertThrows(ValidationException.class, () ->
                bankDetailsConsumer.gettingBankListening(null));
    }

    @Test
    void testGettingBankListening_ShouldHandleException_WhenServiceThrowsException() throws ValidationException {
        RuntimeException exception = new RuntimeException("Get error");
        when(bankDetailsService.getBankDetailsById(1L)).thenThrow(exception);
        bankDetailsConsumer.gettingBankListening(validBankDetailsDto);
        verify(bankDetailsService).getBankDetailsById(1L);
    }
}
