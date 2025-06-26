package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.License;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.mapper.LicenseMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.LicenseRepository;
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

@ExtendWith(MockitoExtension.class)
class LicenseServiceImplTest {

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private LicenseMapper licenseMapper;

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @Mock
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private LicenseServiceImpl licenseService;

    @Value("${spring.kafka.topics.error-log.name}")
    private String errorTopic;

    private License license;
    private LicenseDto licenseDto;
    private BankDetails bankDetails;

    @BeforeEach
    void setUp() {
        bankDetails = new BankDetails();
        bankDetails.setId(1L);

        byte[] photo = new byte[]{1, 2, 3};

        license = License.builder()
                .id(1L)
                .photo(photo)
                .bankDetails(bankDetails)
                .build();

        licenseDto = new LicenseDto();
        licenseDto.setId(1L);
        licenseDto.setPhoto(photo);
        licenseDto.setBankDetailsId(1L);
    }

    @Test
    void testCreateNewLicense_Success() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(licenseMapper.toEntity(licenseDto)).thenReturn(license);
        when(licenseRepository.save(license)).thenReturn(license);
        when(licenseMapper.toDto(license)).thenReturn(licenseDto);

        LicenseDto result = licenseService.createNewLicense(licenseDto);

        assertEquals(licenseDto, result);
        verify(licenseRepository).save(license);
    }

    @Test
    void testCreateNewLicense_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> licenseService.createNewLicense(null));

        assertEquals("Attempt to create null License", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewLicense_BankDetailsNotFound() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> licenseService.createNewLicense(licenseDto));

        assertTrue(ex.getMessage().contains("Bank with ID 1 not found"));
        verify(globalExceptionHandler).handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewLicense_Exception() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(licenseMapper.toEntity(licenseDto)).thenReturn(license);
        when(licenseRepository.save(license)).thenThrow(new RuntimeException("DB Error while creating license"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> licenseService.createNewLicense(licenseDto));

        assertEquals("DB Error while creating license", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testUpdateLicense_Success() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.of(license));
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(licenseRepository.save(license)).thenReturn(license);
        when(licenseMapper.toDto(license)).thenReturn(licenseDto);

        LicenseDto result = licenseService.updateLicense(licenseDto);

        assertEquals(licenseDto, result);
        verify(licenseMapper).updateFromDto(licenseDto, license);
        verify(licenseRepository).save(license);
    }

    @Test
    void testUpdateLicense_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> licenseService.updateLicense(null));

        assertEquals("License DTO must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testUpdateLicense_LicenseNotFound() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> licenseService.updateLicense(licenseDto));

        assertTrue(ex.getMessage().contains("License with ID 1 not found"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testUpdateLicense_BankDetailsNotFound() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.of(license));
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> licenseService.updateLicense(licenseDto));

        assertTrue(ex.getMessage().contains("Bank with ID 1 not found"));
        verify(globalExceptionHandler).handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testUpdateLicense_Exception() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.of(license));
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(licenseRepository.save(license)).thenThrow(new RuntimeException("DB Error while updating the License"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> licenseService.updateLicense(licenseDto));

        assertEquals("DB Error while updating the License", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testDeleteLicense_Success() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.of(license));
        doNothing().when(licenseRepository).delete(license);

        licenseService.deleteLicense(1L);

        verify(licenseRepository).delete(license);
    }

    @Test
    void testDeleteLicense_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> licenseService.deleteLicense(null));

        assertEquals("License ID must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testDeleteLicense_LicenseNotFound() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> licenseService.deleteLicense(1L));

        assertTrue(ex.getMessage().contains("No license with ID 1"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testDeleteLicense_Exception() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.of(license));
        doThrow(new RuntimeException("DB Error while deleting License")).when(licenseRepository).delete(license);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> licenseService.deleteLicense(1L));

        assertEquals("DB Error while deleting License", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testGetLicensesByBankDetails_Success() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(licenseRepository.findLicensesByBankDetails(bankDetails)).thenReturn(List.of(license));
        when(licenseMapper.toDto(license)).thenReturn(licenseDto);

        List<LicenseDto> result = licenseService.getLicensesByBankDetails(1L);

        assertEquals(1, result.size());
        assertEquals(licenseDto, result.get(0));
    }

    @Test
    void testGetLicensesByBankDetails_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> licenseService.getLicensesByBankDetails(null));

        assertEquals("BankDetails ID must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetLicensesByBankDetails_BankDetailsNotFound() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> licenseService.getLicensesByBankDetails(1L));

        assertTrue(ex.getMessage().contains("Bank with ID 1 not found"));
        verify(globalExceptionHandler).handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testGetLicensesByBankDetails_DataAccessException() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(licenseRepository.findLicensesByBankDetails(bankDetails))
                .thenThrow(new DataAccessException("DB Error while retrieving the License"));

        DataAccessException ex = assertThrows(DataAccessException.class,
                () -> licenseService.getLicensesByBankDetails(1L));

        assertEquals("DB Error while retrieving the License", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(DataAccessException.class), eq(errorTopic));
    }

    @Test
    void testGetLicensesByBankDetails_Exception() {
        when(bankDetailsRepository.findById(1L)).thenReturn(Optional.of(bankDetails));
        when(licenseRepository.findLicensesByBankDetails(bankDetails))
                .thenThrow(new RuntimeException("Unexpected Error while retrieving the License"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> licenseService.getLicensesByBankDetails(1L));

        assertEquals("Unexpected Error while retrieving the License", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testGetLicenseById_Success() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.of(license));
        when(licenseMapper.toDto(license)).thenReturn(licenseDto);

        LicenseDto result = licenseService.getLicenseById(1L);

        assertEquals(licenseDto, result);
    }

    @Test
    void testGetLicenseById_NullInput() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> licenseService.getLicenseById(null));

        assertEquals("License ID must not be null", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetLicenseById_LicenseNotFound() {
        when(licenseRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> licenseService.getLicenseById(1L));

        assertTrue(ex.getMessage().contains("License with id 1 not found"));
        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testGetLicenseById_Exception() {
        when(licenseRepository.findById(1L))
                .thenThrow(new RuntimeException("Unexpected Error while retrieving the License"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> licenseService.getLicenseById(1L));

        assertEquals("Unexpected Error while retrieving the License", ex.getMessage());
        verify(globalExceptionHandler).handleException(any(RuntimeException.class), eq(errorTopic));
    }
}
