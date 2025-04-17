package com.bank.publicinfo.service;

import com.bank.publicinfo.config.JacksonMapperConfig;
import com.bank.publicinfo.dto.AuditDto;
import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.entity.Audit;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.exception.CustomJsonProcessingException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.exception.ValidationException;
import com.bank.publicinfo.mapper.AuditMapper;
import com.bank.publicinfo.repository.AuditRepository;
import com.bank.publicinfo.repository.BankDetailsRepository;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static com.bank.publicinfo.enumtype.EntityType.BANK_DETAILS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditRepository auditRepository;
    @Mock
    private AuditMapper auditMapper;
    @Mock
    private BankDetailsRepository bankDetailsRepository;
    @Mock
    private JacksonMapperConfig mapper;
    @Mock
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Value("${spring.kafka.topics.error-log.name}")
    String errorTopic;

    private final Long testId = 1L;
    private BankDetailsDto bankDetailsDto;
    private AuditDto auditDto;

    @BeforeEach
    void setUp() {
        bankDetailsDto = new BankDetailsDto();
        bankDetailsDto.setId(testId);

        auditDto = new AuditDto();
        auditDto.setEntityJson("{\"id\":1,\"oldValue\":\"test\"}");
        auditDto.setEntityType(String.valueOf(BANK_DETAILS));
    }

    @Test
    void testUpdateAudit_ShouldSaveUpdatedAudit_WhenValidInput() throws Exception {
        String newEntityJson = "{\"id\":1}";
        JsonNode newJsonNode = mock(JsonNode.class);
        JsonNode newIdNode = mock(JsonNode.class);

        when(newJsonNode.get("id")).thenReturn(newIdNode);
        when(newIdNode.asLong()).thenReturn(1L);
        when(mapper.writeValueAsString(bankDetailsDto)).thenReturn(newEntityJson);
        when(mapper.readTree(newEntityJson)).thenReturn(newJsonNode);

        Audit existingAudit = new Audit();
        existingAudit.setEntityJson(auditDto.getEntityJson());

        Page<Audit> mockPage = new PageImpl<>(Collections.singletonList(existingAudit));
        when(auditRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
        when(auditMapper.toDto(existingAudit)).thenReturn(auditDto);

        JsonNode existingJsonNode = mock(JsonNode.class);
        JsonNode existingIdNode = mock(JsonNode.class);

        when(mapper.readTree(auditDto.getEntityJson())).thenReturn(existingJsonNode);
        when(existingJsonNode.has("id")).thenReturn(true);
        when(existingJsonNode.get("id")).thenReturn(existingIdNode);
        when(existingIdNode.asLong()).thenReturn(1L);

        Audit newAudit = new Audit();
        when(auditMapper.toEntity(any(AuditDto.class))).thenReturn(newAudit);

        auditService.updateAudit(bankDetailsDto);

        ArgumentCaptor<AuditDto> auditDtoCaptor = ArgumentCaptor.forClass(AuditDto.class);
        verify(auditMapper).toEntity(auditDtoCaptor.capture());

        ArgumentCaptor<Audit> auditCaptor = ArgumentCaptor.forClass(Audit.class);
        verify(auditRepository).save(auditCaptor.capture());

        assertNotNull(auditCaptor.getValue());
        assertEquals(String.valueOf(BANK_DETAILS), auditDtoCaptor.getValue().getEntityType());
    }

    @Test
    void testUpdateAudit_ShouldThrowValidationException_WhenAuditNotFound() throws Exception {
        JsonNode jsonNode = mock(JsonNode.class);
        JsonNode idNode = mock(JsonNode.class);
        when(jsonNode.get("id")).thenReturn(idNode);
        when(idNode.asLong()).thenReturn(1L);
        when(mapper.writeValueAsString(bankDetailsDto)).thenReturn("{\"id\":1}");
        when(mapper.readTree(anyString())).thenReturn(jsonNode);
        when(auditRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        assertThrows(ValidationException.class, () -> auditService.updateAudit(bankDetailsDto));
    }

    @Test
    void testUpdateAudit_ShouldThrowCustomJsonProcessingException_WhenJsonError() throws Exception {
        when(mapper.writeValueAsString(bankDetailsDto)).thenThrow(new CustomJsonProcessingException("Error") {});
        assertThrows(CustomJsonProcessingException.class, () -> auditService.updateAudit(bankDetailsDto));
        verify(globalExceptionHandler).handleException(any(), any());
    }

    @Test
    void testCreateAudit_Success() throws Exception {
        Audit audit = new Audit();
        when(auditMapper.toEntity(any(AuditDto.class))).thenReturn(audit);
        auditService.createAudit(bankDetailsDto);
        verify(auditRepository).save(audit);
        verify(auditMapper).toEntity(any(AuditDto.class));
    }

    @Test
    void testThrowCustomJsonProcessingException() throws Exception {
        when(mapper.writeValueAsString(bankDetailsDto))
                .thenThrow(new CustomJsonProcessingException("JSON processing exception") {});
        assertThrows(CustomJsonProcessingException.class, () -> auditService.createAudit(bankDetailsDto));
        verify(globalExceptionHandler).handleException(any(CustomJsonProcessingException.class), any());
    }

    @Test
    void testReturnAuditById() {
        Audit audit = new Audit();
        audit.setId(testId);

        AuditDto auditDto = new AuditDto();
        auditDto.setId(testId);

        when(auditRepository.findById(testId)).thenReturn(Optional.of(audit));
        when(auditMapper.toDto(audit)).thenReturn(auditDto);

        AuditDto result = auditService.getAuditById(testId);

        assertNotNull(result);
        assertEquals(auditDto, result);
        assertEquals(testId, result.getId());
        verify(auditRepository).findById(testId);
        verify(auditMapper).toDto(audit);
    }

    @Test
    void testThrowExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> auditService.getAuditById(null));
        verify(globalExceptionHandler)
                .handleException(any(IllegalArgumentException.class), any());
    }

    @Test
    void testThrowExceptionWhenNotFound() {
        when(auditRepository.findById(testId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> auditService.getAuditById(testId));
    }

    @Test
    void testDeleteAuditSuccessfully() {
        when(auditRepository.existsById(testId)).thenReturn(true);
        auditService.deleteAuditById(testId);
        verify(auditRepository).deleteById(testId);
    }

    @Test
    void testDeleteAuditThrowExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> auditService.deleteAuditById(null));
    }

    @Test
    void testDeleteAuditThrowExceptionWhenNotFound() {
        when(auditRepository.existsById(testId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> auditService.deleteAuditById(testId));
    }

    @Test
    void testFindBankDetailsByBik_Success() {
        Long bik = 1111L;
        BankDetails bankDetails = new BankDetails();
        bankDetails.setBik(bik);
        when(bankDetailsRepository.findByBik(bik)).thenReturn(bankDetails);
        BankDetails result = auditService.findBankDetailsByBik(bik);
        assertNotNull(result);
        assertEquals(bankDetails, result);
        verify(bankDetailsRepository).findByBik(bik);
    }

    @Test
    void testGetAllAudits_ShouldReturnPageOfAudits_WhenPageableIsValid() {
        Pageable pageable = PageRequest.of(0, 10);
        Audit audit = new Audit();
        audit.setId(1L);
        Page<Audit> auditPage = new PageImpl<>(Collections.singletonList(audit));

        AuditDto auditDto = new AuditDto();
        auditDto.setId(1L);

        when(auditRepository.findAll(pageable)).thenReturn(auditPage);
        when(auditMapper.toDto(audit)).thenReturn(auditDto);

        Page<AuditDto> result = auditService.getAllAudits(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(auditDto, result.getContent().get(0));
        verify(auditRepository).findAll(pageable);
        verify(auditMapper).toDto(audit);
    }

    @Test
    void testGetAllAudits_ShouldThrowIllegalArgumentException_WhenPageableIsNull() {
        String errorMessage = "Page parameters cannot be null";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> auditService.getAllAudits(null)
        );

        assertEquals(errorMessage, exception.getMessage());
        verify(globalExceptionHandler).handleException(any(IllegalArgumentException.class), eq(errorTopic));
        verifyNoInteractions(auditRepository, auditMapper);
    }

    @Test
    void testFindBankDetailsByBik_ShouldThrowEntityNotFoundException() {
        Long bik = 1111L;
        when(bankDetailsRepository.findByBik(bik)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> auditService.findBankDetailsByBik(bik)
        );

        assertEquals("Bank details not found for BIK: " + bik, exception.getMessage());
        verify(bankDetailsRepository).findByBik(bik);
    }

    @Test
    void testThrowExceptionWhenBikIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> auditService.findBankDetailsByBik(null));
    }

    @Test
    void testFindThrowExceptionWhenNotFound() {
        when(bankDetailsRepository.findByBik(anyLong())).thenReturn(null);
        assertThrows(EntityNotFoundException.class,
                () -> auditService.findBankDetailsByBik(1111L));
    }
}
