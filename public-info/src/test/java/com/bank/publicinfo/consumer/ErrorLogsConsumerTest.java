package com.bank.publicinfo.consumer;

import com.bank.publicinfo.dto.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ErrorLogsConsumerTest {

    @InjectMocks
    private ErrorLogsConsumer errorLogsConsumer;

    private ErrorResponseDto errorResponseDto;

    @BeforeEach
    void setUp() {
        errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setErrorCode("ERR-001");
        errorResponseDto.setMessage("Test error message");
        errorResponseDto.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testListen_ShouldLogErrorDetails_WhenValidErrorResponseReceived() {
        errorLogsConsumer.listen(errorResponseDto);
    }

    @Test
    void testListen_ShouldLogWarning_WhenErrorResponseIsNull() {
        errorLogsConsumer.listen(null);
    }

    @Test
    void testListen_ShouldLogPartialDetails_WhenErrorResponseHasNullFields() {
        ErrorResponseDto partialErrorResponse = new ErrorResponseDto();
        partialErrorResponse.setErrorCode(null);
        partialErrorResponse.setMessage("Partial error");
        partialErrorResponse.setTimestamp(null);
        errorLogsConsumer.listen(partialErrorResponse);
    }
}
