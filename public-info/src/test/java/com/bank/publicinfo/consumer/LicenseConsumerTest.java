package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.service.LicenseService;
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
class LicenseConsumerTest {

    @Mock
    private LicenseService licenseService;

    @InjectMocks
    private LicenseConsumer licenseConsumer;

    private LicenseDto validLicenseDto;
    private LicenseDto nullIdLicenseDto;

    @BeforeEach
    void setUp() {
        validLicenseDto = new LicenseDto();
        validLicenseDto.setId(1L);
        validLicenseDto.setPhoto(new byte[]{1, 2, 3});

        nullIdLicenseDto = new LicenseDto();
        nullIdLicenseDto.setId(null);
        nullIdLicenseDto.setPhoto(new byte[]{1, 2, 3});
    }

    @Test
    void creatingLicenseListening_ShouldCreateSuccess_WhenValidInput() {
        LicenseDto savedLicenseDto = new LicenseDto();
        savedLicenseDto.setId(1L);
        when(licenseService.createNewLicense(validLicenseDto)).thenReturn(savedLicenseDto);
        LicenseDto result = licenseConsumer.creatingLicenseListening(validLicenseDto);
        assertEquals(savedLicenseDto, result);
    }

    @Test
    void creatingLicenseListening_ShouldThrowException_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Service error");
        when(licenseService.createNewLicense(validLicenseDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () ->
                licenseConsumer.creatingLicenseListening(validLicenseDto));
    }

    @Test
    void updatingLicenseListening_ShouldUpdateSuccess_WhenValidInput() {
        when(licenseService.updateLicense(validLicenseDto)).thenReturn(validLicenseDto);
        LicenseDto result = licenseConsumer.updatingLicenseListening(validLicenseDto);
        assertEquals(validLicenseDto, result);
    }

    @Test
    void updatingLicenseListening_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> licenseConsumer.updatingLicenseListening(nullIdLicenseDto)
        );
        assertEquals("License ID is null", exception.getMessage());
    }

    @Test
    void updatingLicenseListening_ShouldThrowException_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Update error");
        when(licenseService.updateLicense(validLicenseDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () ->
                licenseConsumer.updatingLicenseListening(validLicenseDto));
    }

    @Test
    void deletingLicenseListening_ShouldDeleteSuccess_WhenValidInput() {
        licenseConsumer.deletingLicenseListening(validLicenseDto);
        verify(licenseService).deleteLicense(1L);
    }

    @Test
    void deletingLicenseListening_ShouldDoNothing_WhenIdIsNull() {
        licenseConsumer.deletingLicenseListening(nullIdLicenseDto);
        verifyNoInteractions(licenseService);
    }

    @Test
    void gettingLicenseListening_ShouldGetSuccess_WhenValidInput() {
        when(licenseService.getLicenseById(1L)).thenReturn(validLicenseDto);
        licenseConsumer.gettingLicenseListening(validLicenseDto);
        verify(licenseService).getLicenseById(1L);
    }

    @Test
    void gettingLicenseListening_ShouldDoNothing_WhenIdIsNull() {
        licenseConsumer.gettingLicenseListening(nullIdLicenseDto);
        verifyNoInteractions(licenseService);
    }

    @Test
    void gettingLicenseListening_ShouldThrowException_WhenInputIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                licenseConsumer.gettingLicenseListening(null));
    }

    @Test
    void gettingLicenseListening_ShouldHandleException_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Get error");
        when(licenseService.getLicenseById(1L)).thenThrow(exception);
        licenseConsumer.gettingLicenseListening(validLicenseDto);
        verify(licenseService).getLicenseById(1L);
    }
}
