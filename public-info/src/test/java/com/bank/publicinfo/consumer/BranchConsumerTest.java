package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.service.BranchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.bank.publicinfo.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchConsumerTest {

    @Mock
    private BranchService branchService;

    @InjectMocks
    private BranchConsumer branchConsumer;

    private BranchDto validBranchDto;
    private BranchDto nullIdBranchDto;

    @BeforeEach
    void setUp() {
        validBranchDto = new BranchDto();
        validBranchDto.setId(1L);
        validBranchDto.setAddress("Test Address");

        nullIdBranchDto = new BranchDto();
        nullIdBranchDto.setId(null);
        nullIdBranchDto.setAddress("Test Address");
    }

    @Test
    void testCreatingBranchListening_ShouldCreateSuccess_WhenValidInput() throws ValidationException {
        BranchDto savedBranchDto = new BranchDto();
        savedBranchDto.setId(1L);
        when(branchService.createNewBranch(validBranchDto)).thenReturn(savedBranchDto);
        BranchDto result = branchConsumer.creatingBranchListening(validBranchDto);
        assertEquals(savedBranchDto, result);
    }

    @Test
    void testCreatingBranchListening_ShouldThrowException_WhenServiceThrowsException() throws ValidationException {
        RuntimeException exception = new RuntimeException("Service error");
        when(branchService.createNewBranch(validBranchDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () ->
                branchConsumer.creatingBranchListening(validBranchDto));
    }

    @Test
    void testUpdatingBranchListening_ShouldUpdateSuccess_WhenValidInput() {
        when(branchService.updateBranch(1L, validBranchDto)).thenReturn(validBranchDto);
        BranchDto result = branchConsumer.updatingBranchListening(validBranchDto);
        assertEquals(validBranchDto, result);
    }

    @Test
    void testUpdatingBranchListening_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> branchConsumer.updatingBranchListening(nullIdBranchDto)
        );
        assertEquals("Branch ID is null", exception.getMessage());
    }

    @Test
    void testUpdatingBranchListening_ShouldThrowException_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Update error");
        when(branchService.updateBranch(1L, validBranchDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () ->
                branchConsumer.updatingBranchListening(validBranchDto));
    }

    @Test
    void testDeletingBranchListening_ShouldDeleteSuccess_WhenValidInput() {
        branchConsumer.deletingBranchListening(validBranchDto);
        verify(branchService).deleteBranchById(1L);
    }

    @Test
    void testDeletingBranchListening_ShouldDoNothing_WhenIdIsNull() {
        branchConsumer.deletingBranchListening(nullIdBranchDto);
        verifyNoInteractions(branchService);
    }

    @Test
    void testGettingBranchListening_ShouldGetSuccess_WhenValidInput() throws ValidationException {
        when(branchService.getBranchById(1L)).thenReturn(validBranchDto);
        branchConsumer.gettingBranchListening(validBranchDto);
        verify(branchService).getBranchById(1L);
    }

    @Test
    void testGettingBranchListening_ShouldDoNothing_WhenIdIsNull() throws ValidationException {
        branchConsumer.gettingBranchListening(nullIdBranchDto);
        verifyNoInteractions(branchService);
    }

    @Test
    void testGettingBranchListening_ShouldThrowException_WhenInputIsNull() {
        assertThrows(ValidationException.class, () ->
                branchConsumer.gettingBranchListening(null));
    }

    @Test
    void testGettingBranchListening_ShouldHandleException_WhenServiceThrowsException() throws ValidationException {
        RuntimeException exception = new RuntimeException("Get error");
        when(branchService.getBranchById(1L)).thenThrow(exception);
        branchConsumer.gettingBranchListening(validBranchDto);
        verify(branchService).getBranchById(1L);
    }
}
