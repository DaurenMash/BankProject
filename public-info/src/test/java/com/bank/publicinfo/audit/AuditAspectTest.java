package com.bank.publicinfo.audit;

import com.bank.publicinfo.exception.CustomJsonProcessingException;
import com.bank.publicinfo.service.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private static AuditService auditService;

    @InjectMocks
    private AuditAspect auditAspect;

    private static Logger logger;

    @BeforeAll
    static void setupLogger() {
        logger = mock(Logger.class);
        try (MockedStatic<LoggerFactory> mocked = mockStatic(LoggerFactory.class)) {
            mocked.when(() -> LoggerFactory.getLogger(AuditAspect.class)).thenReturn(logger);
            new AuditAspect(auditService);
        }
    }

    @BeforeEach
    void setUp() {
        reset(auditService, logger);
    }

    @Test
    void testAfterCreateAdvice_ShouldCallCreateAudit_WhenResultNotNull() throws JsonProcessingException {
        Object testDto = new Object();
        auditAspect.afterCreateAdvice(testDto);
        verify(auditService).createAudit(testDto);
        verifyNoInteractions(logger);
    }

    @Test
    void testAfterCreateAdvice_ShouldNotCallCreateAudit_WhenResultNull() throws JsonProcessingException {
        auditAspect.afterCreateAdvice(null);
        verify(auditService, never()).createAudit(any());
        verifyNoInteractions(logger);
    }

    @Test
    void testAfterCreateAdvice_ShouldLogError_WhenAuditServiceThrowsException()
            throws JsonProcessingException {
        Object testDto = new Object();
        RuntimeException exception = new RuntimeException("Audit service error");
        doThrow(exception).when(auditService).createAudit(any());
        auditAspect.afterCreateAdvice(testDto);
        verify(logger).error("Fail in afterCreateAdvice for: {}. Error: ", testDto, exception);
    }

    @Test
    void testAfterUpdateAdvice_ShouldCallUpdateAudit_WhenResultNotNull()
            throws ValidationException, JsonProcessingException {
        Object testDto = new Object();
        auditAspect.afterUpdateAdvice(testDto);
        verify(auditService).updateAudit(testDto);
        verifyNoInteractions(logger);
    }

    @Test
    void testAfterUpdateAdvice_ShouldNotCallUpdateAudit_WhenResultNull()
            throws ValidationException, JsonProcessingException {
        auditAspect.afterUpdateAdvice(null);
        verify(auditService, never()).updateAudit(any());
        verifyNoInteractions(logger);
    }

    @Test
    void testAfterUpdateAdvice_ShouldLogError_WhenAuditServiceThrowsException()
            throws ValidationException, JsonProcessingException {
        Object testDto = new Object();
        RuntimeException exception = new RuntimeException("Audit service error");
        doThrow(exception).when(auditService).updateAudit(any());
        auditAspect.afterUpdateAdvice(testDto);
        verify(logger).error("Fail in afterUpdateAdvice for: {}. Error: ", testDto, exception);
    }
}
