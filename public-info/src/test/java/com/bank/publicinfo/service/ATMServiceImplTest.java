package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.ATMDto;
import com.bank.publicinfo.entity.ATM;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.mapper.ATMMapper;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
public class ATMServiceImplTest {

    @Mock
    private AtmRepository atmRepository;

    @Mock
    private ATMMapper atmMapper;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private ATMServiceImpl atmService;

    @Value("${spring.kafka.topics.error-log.name}")
    private String errorTopic;

    private ATM atm;
    private ATMDto atmDto;
    private Branch branch;

    @BeforeEach
    void setUp() {
        branch = new Branch();
        branch.setId(1L);
        branch.setAddress("Branch Address");

        atm = new ATM();
        atm.setId(1L);
        atm.setAddress("ATM Address");
        atm.setStartOfWork(LocalTime.of(8, 0));
        atm.setEndOfWork(LocalTime.of(20, 0));
        atm.setAllHours(false);
        atm.setBranch(branch);

        atmDto = new ATMDto();
        atmDto.setId(1L);
        atmDto.setAddress("ATM Address");
        atmDto.setStartOfWork(LocalTime.of(8, 0));
        atmDto.setEndOfWork(LocalTime.of(20, 0));
        atmDto.setAllHours(false);
        atmDto.setBranchId(1L);
    }

    @Test
    void testCreateNewATM_Success() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(atmMapper.toEntity(atmDto)).thenReturn(atm);
        when(atmRepository.save(atm)).thenReturn(atm);
        when(atmMapper.toDto(atm)).thenReturn(atmDto);
        ATMDto result = atmService.createNewATM(atmDto);
        assertEquals(atmDto, result);
        verify(atmRepository).save(atm);
    }

    @Test
    void testCreateNewATM_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> atmService.createNewATM(null));
        assertEquals("It is impossible to create null ATM", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewATM_BranchNotFound() {
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> atmService.createNewATM(atmDto));
        assertTrue(ex.getMessage().contains("No any branch for ATM id: 1"));
        verify(globalExceptionHandler).handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewATM_Exception() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(atmMapper.toEntity(atmDto)).thenReturn(atm);
        when(atmRepository.save(atm)).thenThrow(new RuntimeException("DB Error while creating ATM"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.createNewATM(atmDto));
        assertEquals("DB Error while creating ATM", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testUpdateATM_Success() {
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(atmRepository.save(atm)).thenReturn(atm);
        when(atmMapper.toDto(atm)).thenReturn(atmDto);
        ATMDto result = atmService.updateATM(atmDto);
        assertEquals(atmDto, result);
        verify(atmRepository).save(atm);
    }

    @Test
    void testUpdateATM_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> atmService.updateATM(null));
        assertEquals("ATM DTO must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testUpdateATM_ATMNotFound() {
        when(atmRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> atmService.updateATM(atmDto));
        assertTrue(ex.getMessage().contains("ATM not found with ID: 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testUpdateATM_BranchNotFound() {
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> atmService.updateATM(atmDto));
        assertTrue(ex.getMessage().contains("Branch not found for id: 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testUpdateATM_Exception() {
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(atmRepository.save(atm)).thenThrow(new RuntimeException("DB Error while updating ATM"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.updateATM(atmDto));
        assertEquals("DB Error while updating ATM", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testDeleteATMById_Success() {
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));
        doNothing().when(atmRepository).delete(atm);
        atmService.deleteATMById(1L);
        verify(atmRepository).delete(atm);
    }

    @Test
    void testDeleteATMById_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> atmService.deleteATMById(null));
        assertEquals("ATM ID can't be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testDeleteATMById_ATMNotFound() {
        when(atmRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> atmService.deleteATMById(1L));
        assertTrue(ex.getMessage().contains("No ATM with id 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testDeleteATMById_Exception() {
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));
        doThrow(new RuntimeException("DB Error while deleting ATM")).when(atmRepository).delete(atm);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.deleteATMById(1L));
        assertEquals("DB Error while deleting ATM", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testGetATMs_Success() {
        when(atmRepository.findByBranchId(1L)).thenReturn(List.of(atm));
        when(atmMapper.toDto(atm)).thenReturn(atmDto);
        List<ATMDto> result = atmService.getATMs(1L);
        assertEquals(1, result.size());
        assertEquals(atmDto, result.get(0));
    }

    @Test
    void testGetATMs_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> atmService.getATMs(null));
        assertEquals("Branch ID must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetATMs_DataAccessException() {
        when(atmRepository.findByBranchId(1L)).thenThrow(new DataAccessException("DB Error while retrieving ATMs") {});
        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> atmService.getATMs(1L));
        assertEquals("DB Error while retrieving ATMs", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(DataAccessException.class), eq(errorTopic));
    }

    @Test
    void testGetATMs_Exception() {
        when(atmRepository.findByBranchId(1L)).thenThrow(new RuntimeException("Unexpected Error"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.getATMs(1L));
        assertEquals("Unexpected Error", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testGetATMById_Success() {
        when(atmRepository.findById(1L)).thenReturn(Optional.of(atm));
        when(atmMapper.toDto(atm)).thenReturn(atmDto);
        ATMDto result = atmService.getATMById(1L);
        assertEquals(atmDto, result);
    }

    @Test
    void testGetATMById_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> atmService.getATMById(null));
        assertEquals("ATM ID must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetATMById_ATMNotFound() {
        when(atmRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> atmService.getATMById(1L));
        assertTrue(ex.getMessage().contains("There is no ATM with id 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testGetATMById_Exception() {
        when(atmRepository.findById(1L)).thenThrow(new RuntimeException("DB Error while retrieving ATM"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.getATMById(1L));
        assertEquals("DB Error while retrieving ATM", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }
}
