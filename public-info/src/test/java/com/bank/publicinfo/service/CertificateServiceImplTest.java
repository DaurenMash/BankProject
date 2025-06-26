package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.Certificate;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.CertificateMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private CertificateMapper certificateMapper;

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @Mock
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private CertificateServiceImpl certificateService;

    @Value("${spring.kafka.topics.error-log.name}")
    private String errorTopic;

    private Certificate certificate;
    private CertificateDto certificateDto;
    private BankDetails bankDetails;

    @BeforeEach
    void setUp() {
        bankDetails = new BankDetails();
        bankDetails.setId(1L);

        byte[] photo = new byte[]{1, 2, 3};

        certificate = Certificate.builder()
                .id(1L)
                .photo(photo)
                .bankDetails(bankDetails)
                .build();

        certificateDto = new CertificateDto();
        certificateDto.setId(1L);
        certificateDto.setPhoto(photo);
        certificateDto.setBankDetailsId(1L);
    }

    @Test
    void testCreateNewCertificate_Success() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(certificateMapper.toEntity(certificateDto)).thenReturn(certificate);
        when(certificateRepository.save(certificate)).thenReturn(certificate);
        when(certificateMapper.toDto(certificate)).thenReturn(certificateDto);

        CertificateDto result = certificateService.createNewCertificate(certificateDto);

        assertEquals(certificateDto, result);
        verify(certificateRepository).save(certificate);
    }

    @Test
    void testCreateNewCertificate_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> certificateService.createNewCertificate(null));

        assertEquals("Attempt to create null Certificate", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewCertificate_BankDetailsNotFound() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> certificateService.createNewCertificate(certificateDto));

        assertTrue(ex.getMessage().contains("Bank with ID 1 not found"));
        verify(globalExceptionHandler).handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewCertificate_Exception() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(certificateMapper.toEntity(certificateDto)).thenReturn(certificate);
        when(certificateRepository.save(certificate)).thenThrow(new RuntimeException("DB Error while creating certificate"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> certificateService.createNewCertificate(certificateDto));

        assertEquals("DB Error while creating certificate", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testUpdateCertificate_Success() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(certificateRepository.save(certificate)).thenReturn(certificate);
        when(certificateMapper.toDto(certificate)).thenReturn(certificateDto);

        CertificateDto result = certificateService.updateCertificate(certificateDto);

        assertEquals(certificateDto, result);
        verify(certificateMapper).updateFromDto(certificateDto, certificate);
        verify(certificateRepository).save(certificate);
    }

    @Test
    void testUpdateCertificate_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> certificateService.updateCertificate(null));

        assertEquals("Certificate DTO must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testUpdateCertificate_CertificateNotFound() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> certificateService.updateCertificate(certificateDto));

        assertTrue(ex.getMessage().contains("No certificate with ID: 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testUpdateCertificate_BankDetailsNotFound() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> certificateService.updateCertificate(certificateDto));

        assertTrue(ex.getMessage().contains("Bank with ID 1 not found"));
        verify(globalExceptionHandler).handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testUpdateCertificate_Exception() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(certificateRepository.save(certificate)).thenThrow(new RuntimeException("DB Error while updating the Certificate"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> certificateService.updateCertificate(certificateDto));

        assertEquals("DB Error while updating the Certificate", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testDeleteCertificate_Success() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        doNothing().when(certificateRepository).delete(certificate);

        certificateService.deleteCertificate(1L);

        verify(certificateRepository).delete(certificate);
    }

    @Test
    void testDeleteCertificate_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> certificateService.deleteCertificate(null));

        assertEquals("Certificate id must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testDeleteCertificate_CertificateNotFound() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> certificateService.deleteCertificate(1L));

        assertTrue(ex.getMessage().contains("Certificate with ID not found 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testDeleteCertificate_Exception() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        doThrow(new RuntimeException("DB Error while deleting Certificate")).when(certificateRepository).delete(certificate);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> certificateService.deleteCertificate(1L));

        assertEquals("DB Error while deleting Certificate", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testGetCertificatesByBankDetails_Success() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(certificateRepository.findCertificatesByBankDetails(bankDetails)).thenReturn(List.of(certificate));
        when(certificateMapper.toDto(certificate)).thenReturn(certificateDto);

        List<CertificateDto> result = certificateService.getCertificatesByBankDetails(1L);

        assertEquals(1, result.size());
        assertEquals(certificateDto, result.get(0));
    }

    @Test
    void testGetCertificatesByBankDetails_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> certificateService.getCertificatesByBankDetails(null));

        assertEquals("BankDetails ID must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetCertificatesByBankDetails_BankDetailsNotFound() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> certificateService.getCertificatesByBankDetails(1L));

        assertTrue(ex.getMessage().contains("Bank with ID 1 not found"));
        verify(globalExceptionHandler).handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testGetCertificatesByBankDetails_DataAccessException() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(certificateRepository.findCertificatesByBankDetails(bankDetails))
                .thenThrow(new DataAccessException("DB Error while retrieving the Certificate"));

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> certificateService.getCertificatesByBankDetails(1L));

        assertEquals("DB Error while retrieving the Certificate", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(DataAccessException.class), eq(errorTopic));
    }

    @Test
    void testGetCertificatesByBankDetails_Exception() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(certificateRepository.findCertificatesByBankDetails(bankDetails))
                .thenThrow(new RuntimeException("Unexpected Error while retrieving the Certificate"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> certificateService.getCertificatesByBankDetails(1L));

        assertEquals("Unexpected Error while retrieving the Certificate", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testGetCertificateById_Success() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(certificateMapper.toDto(certificate)).thenReturn(certificateDto);

        CertificateDto result = certificateService.getCertificateById(1L);

        assertEquals(certificateDto, result);
    }

    @Test
    void testGetCertificateById_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> certificateService.getCertificateById(null));

        assertEquals("Certificate ID must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetCertificateById_CertificateNotFound() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> certificateService.getCertificateById(1L));

        assertTrue(ex.getMessage().contains("No certificate with ID 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testGetCertificateById_Exception() {
        when(certificateRepository.findById(1L))
                .thenThrow(new RuntimeException("Unexpected Error while retrieving the Certificate"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> certificateService.getCertificateById(1L));

        assertEquals("Unexpected Error while retrieving the Certificate", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }
}
