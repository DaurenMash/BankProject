package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.exception.ValidationException;
import com.bank.publicinfo.mapper.BankDetailsMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import com.bank.publicinfo.repository.LicenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BankDetailsServiceImplTest {

    @Mock
    private BankDetailsRepository bankDetailsRepository;
    @Mock
    private BankDetailsMapper bankDetailsMapper;
    @Mock
    private GlobalExceptionHandler globalExceptionHandler;
    @Mock
    private LicenseRepository licenseRepository;
    @Mock
    private CertificateRepository certificateRepository;
    @InjectMocks
    private BankDetailsServiceImpl bankDetailsService;

    private BankDetails bankDetails;
    private BankDetailsDto bankDetailsDto;

    @Value("${spring.kafka.topics.error-log.name}")
    String errorTopic;

    @BeforeEach
    void setUp() {
        bankDetailsDto = BankDetailsDto.builder()
                .id(123L)
                .bik(1111L)
                .inn(2222L)
                .kpp(3333L)
                .corAccount(4444L)
                .city("Saratov")
                .jointStockCompany("JSC")
                .name("TorgPredBank")
                .build();

        bankDetails = BankDetails.builder()
                .id(123L)
                .bik(1111L)
                .inn(2222L)
                .kpp(3333L)
                .corAccount(4444L)
                .city("Saratov")
                .jointStockCompany("JSC")
                .name("TorgPredBank")
                .build();
    }

    @Test
    void testCreateNewBankDetails_Success() {
        when(bankDetailsMapper.toEntity(bankDetailsDto)).thenReturn(bankDetails);
        when(bankDetailsMapper.toDto(bankDetails)).thenReturn(bankDetailsDto);
        when(bankDetailsRepository.save(bankDetails)).thenReturn(bankDetails);
        BankDetailsDto result = bankDetailsService.createNewBankDetails(bankDetailsDto);
        assertEquals(bankDetailsDto, result);
        verify(bankDetailsRepository, times(1)).save(bankDetails);
    }

    @Test
    void testCreateNewBankDetails_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankDetailsService.createNewBankDetails(null);
        });
    }

    @Test
    void testCreateNewBankDetails_DataAccessException() {
        when(bankDetailsMapper.toEntity(bankDetailsDto)).thenReturn(bankDetails);
        when(bankDetailsRepository.save(bankDetails)).thenThrow(new DataAccessException("Database error") {});
        assertThrows(DataAccessException.class, () -> {
            bankDetailsService.createNewBankDetails(bankDetailsDto);
        });
        verify(globalExceptionHandler, times(1))
                .handleException(any(DataAccessException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewBankDetails_MapperFailure() {
        when(bankDetailsMapper.toEntity(bankDetailsDto))
                .thenThrow(new RuntimeException("Mapping error"));
        assertThrows(RuntimeException.class, () -> {
            bankDetailsService.createNewBankDetails(bankDetailsDto);
        });
        verify(globalExceptionHandler, times(1))
                .handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewBankDetails_LoggingVerification() {
        when(bankDetailsMapper.toEntity(bankDetailsDto)).thenReturn(bankDetails);
        when(bankDetailsMapper.toDto(bankDetails)).thenReturn(bankDetailsDto);
        when(bankDetailsRepository.save(bankDetails)).thenReturn(bankDetails);
        bankDetailsService.createNewBankDetails(bankDetailsDto);
    }

    @Test
    void testUpdateBankDetails_Success() throws ValidationException {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));
        when(bankDetailsMapper.toDto(bankDetails)).thenReturn(bankDetailsDto);
        when(bankDetailsRepository.save(bankDetails)).thenReturn(bankDetails);
        BankDetailsDto result = bankDetailsService.updateBankDetails(bankDetailsDto);
        assertEquals(bankDetailsDto, result);
        verify(bankDetailsRepository, times(1)).save(bankDetails);
    }

    @Test
    void testUpdateBankDetails_DataAccessException() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));
        when(bankDetailsRepository.save(bankDetails))
                .thenThrow(new DataAccessException("Database error") {});
        assertThrows(DataAccessException.class, () -> {
            bankDetailsService.updateBankDetails(bankDetailsDto);
        });
        verify(globalExceptionHandler, times(1))
                .handleException(any(DataAccessException.class), eq(errorTopic));
    }

    @Test
    void testUpdateBankDetails_NullId() {
        BankDetailsDto dtoWithNullId = BankDetailsDto.builder().build();
        assertThrows(ValidationException.class, () -> {
            bankDetailsService.updateBankDetails(dtoWithNullId);
        });
    }

    @Test
    void testUpdateBankDetails_IllegalArgumentException() {
        // Arrange
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));

        IllegalArgumentException originalException = new IllegalArgumentException("Invalid argument");
        doThrow(originalException)
                .when(bankDetailsMapper).updateFromDto(bankDetailsDto, bankDetails);

        // Act & Assert
        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            bankDetailsService.updateBankDetails(bankDetailsDto);
        });
    }

    @Test
    void testUpdateBankDetails_BankNotFound() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            bankDetailsService.updateBankDetails(bankDetailsDto);
        });
    }

    @Test
    void testUpdateBankDetails_MappingVerification() throws ValidationException {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));
        when(bankDetailsMapper.toDto(bankDetails)).thenReturn(bankDetailsDto);
        when(bankDetailsRepository.save(bankDetails)).thenReturn(bankDetails);
        BankDetailsDto result = bankDetailsService.updateBankDetails(bankDetailsDto);
        verify(bankDetailsMapper, times(1)).updateFromDto(bankDetailsDto, bankDetails);
        assertEquals(bankDetailsDto, result);
    }

    @Test
    void testDeleteBankDetails_Success() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));
        doNothing().when(licenseRepository).deleteLicensesByBankDetailsId(123L);
        doNothing().when(certificateRepository).deleteCertificateByBankDetailsId(123L);
        bankDetailsService.deleteBankDetailsById(123L);
        verify(bankDetailsRepository, times(1)).findById(123L);
        verify(bankDetailsRepository, times(1)).delete(bankDetails);
        verify(licenseRepository, times(1)).deleteLicensesByBankDetailsId(123L);
        verify(certificateRepository, times(1)).deleteCertificateByBankDetailsId(123L);
    }

    @Test
    void testDeleteBankDetails_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankDetailsService.deleteBankDetailsById(null);
        });
    }

    @Test
    void testDeleteBankDetails_EntityNotFoundException() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            bankDetailsService.deleteBankDetailsById(123L);
        });
        verify(globalExceptionHandler, times(1))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testDeleteBankDetails_DataAccessException() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));
        doThrow(new DataAccessException("Database error") {})
                .when(licenseRepository).deleteLicensesByBankDetailsId(123L);
        assertThrows(DataAccessException.class, () -> {
            bankDetailsService.deleteBankDetailsById(123L);
        });
        verify(globalExceptionHandler, times(1))
                .handleException(any(DataAccessException.class), eq(errorTopic));
    }

    @Test
    void testDeleteBankDetails_UnexpectedException() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));
        doThrow(new RuntimeException("Unexpected error"))
                .when(certificateRepository).deleteCertificateByBankDetailsId(123L);
        assertThrows(RuntimeException.class, () -> {
            bankDetailsService.deleteBankDetailsById(123L);
        });
        verify(globalExceptionHandler, times(1))
                .handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testGetAllBankDetails_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BankDetails> list = List.of(bankDetails);
        Page<BankDetails> page = new PageImpl<>(list, pageable, 1);
        when(bankDetailsRepository.findAll(pageable)).thenReturn(page);
        when(bankDetailsMapper.toDto(bankDetails)).thenReturn(bankDetailsDto);
        List<BankDetailsDto> result = bankDetailsService.getAllBanksDetails(pageable);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bankDetailsDto, result.get(0));
        verify(bankDetailsRepository).findAll(pageable);
    }

    @Test
    void testGetBankDetails_BankNotFound() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            bankDetailsService.getBankDetailsById(123L);
        });
    }

    @Test
    void testGetBankDetailsById_Success() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.ofNullable(bankDetails));
        when(bankDetailsMapper.toDto(bankDetails)).thenReturn(bankDetailsDto);
        BankDetailsDto result = bankDetailsService.getBankDetailsById(123L);
        assertEquals(bankDetailsDto, result);
        verify(bankDetailsMapper, times(1)).toDto(bankDetails);
    }

    @Test
    void testGetBankDetailsById_LoggingVerification() {
        when(bankDetailsRepository.findById(123L)).thenReturn(Optional.of(bankDetails));
        when(bankDetailsMapper.toDto(bankDetails)).thenReturn(bankDetailsDto);
        bankDetailsService.getBankDetailsById(123L);
    }

    @Test
    void testGetAllBanksDetails_DataAccessException() {
        Pageable pageable = PageRequest.of(0, 10);
        DataAccessException dataAccessException = new DataAccessException("Database error");
        when(bankDetailsRepository.findAll(pageable)).thenThrow(dataAccessException);
        assertThrows(RuntimeException.class, () -> {
            bankDetailsService.getAllBanksDetails(pageable);
        });
        verify(bankDetailsRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllBanksDetails_UnexpectedException() {
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException unexpectedException = new RuntimeException("Unexpected error");
        when(bankDetailsRepository.findAll(pageable)).thenThrow(unexpectedException);
        assertThrows(RuntimeException.class, () -> {
            bankDetailsService.getAllBanksDetails(pageable);
        });
        verify(bankDetailsRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetBankDetails_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankDetailsService.getBankDetailsById(null);
        });
    }

    @Test
    void testGetAllBanksDetails_NullPageable() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankDetailsService.getAllBanksDetails(null);
        });
    }

}
