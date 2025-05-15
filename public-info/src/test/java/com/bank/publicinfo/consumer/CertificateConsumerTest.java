package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.service.CertificateService;
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
class CertificateConsumerTest {

    @Mock
    private CertificateService certificateService;

    @InjectMocks
    private CertificateConsumer certificateConsumer;

    private CertificateDto validCertificateDto;
    private CertificateDto nullIdCertificateDto;

    @BeforeEach
    void setUp() {
        validCertificateDto = new CertificateDto();
        validCertificateDto.setId(1L);
        validCertificateDto.setPhoto(new byte[]{1, 2, 3});

        nullIdCertificateDto = new CertificateDto();
        nullIdCertificateDto.setId(null);
        nullIdCertificateDto.setPhoto(new byte[]{1, 2, 3});
    }

    @Test
    void testCreatingCertificateListening_ShouldCreateSuccess_WhenValidInput() {
        CertificateDto savedCertificateDto = new CertificateDto();
        savedCertificateDto.setId(1L);
        when(certificateService.createNewCertificate(validCertificateDto))
                .thenReturn(savedCertificateDto);
        CertificateDto result = certificateConsumer
                .creatingCertificateListening(validCertificateDto);
        assertEquals(savedCertificateDto, result);
    }

    @Test
    void testCreatingCertificateListening_ShouldThrowException_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Service error");
        when(certificateService.createNewCertificate(validCertificateDto))
                .thenThrow(exception);
        assertThrows(RuntimeException.class, () ->
                certificateConsumer.creatingCertificateListening(validCertificateDto));
    }

    @Test
    void testUpdatingCertificateListening_ShouldUpdateSuccess_WhenValidInput() {
        when(certificateService.updateCertificate(validCertificateDto))
                .thenReturn(validCertificateDto);
        CertificateDto result = certificateConsumer
                .updatingCertificateListening(validCertificateDto);
        assertEquals(validCertificateDto, result);
    }

    @Test
    void testUpdatingCertificateListening_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> certificateConsumer.updatingCertificateListening(nullIdCertificateDto)
        );
        assertEquals("Certificate ID is null", exception.getMessage());
    }

    @Test
    void testUpdatingCertificateListening_ShouldThrowException_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Update error");
        when(certificateService.updateCertificate(validCertificateDto)).thenThrow(exception);
        assertThrows(RuntimeException.class, () ->
                certificateConsumer.updatingCertificateListening(validCertificateDto));
    }

    @Test
    void testDeletingCertificateListening_ShouldDeleteSuccess_WhenValidInput() {
        certificateConsumer.deletingCertificateListening(validCertificateDto);
        verify(certificateService).deleteCertificate(1L);
    }

    @Test
    void testDeletingCertificateListening_ShouldDoNothing_WhenIdIsNull() {
        certificateConsumer.deletingCertificateListening(nullIdCertificateDto);
        verifyNoInteractions(certificateService);
    }

    @Test
    void testGettingCertificateListening_ShouldGetSuccess_WhenValidInput() {
        when(certificateService.getCertificateById(1L)).thenReturn(validCertificateDto);
        certificateConsumer.gettingCertificateListening(validCertificateDto);
        verify(certificateService).getCertificateById(1L);
    }

    @Test
    void testGettingCertificateListening_ShouldDoNothing_WhenIdIsNull() {
        certificateConsumer.gettingCertificateListening(nullIdCertificateDto);
        verifyNoInteractions(certificateService);
    }

    @Test
    void testGettingCertificateListening_ShouldThrowException_WhenInputIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                certificateConsumer.gettingCertificateListening(null));
    }

    @Test
    void testGettingCertificateListening_ShouldHandleException_WhenServiceThrowsException() {
        RuntimeException exception = new RuntimeException("Get error");
        when(certificateService.getCertificateById(1L)).thenThrow(exception);
        certificateConsumer.gettingCertificateListening(validCertificateDto);
        verify(certificateService).getCertificateById(1L);
    }
}
