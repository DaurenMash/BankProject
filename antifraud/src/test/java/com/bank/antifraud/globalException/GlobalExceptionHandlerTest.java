package com.bank.antifraud.globalException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @Mock
    private EntityNotFoundException entityNotFoundException;

    @Mock
    private Exception genericException;

    @Mock
    private WebRequest webRequest;

    @Mock
    private FieldError fieldError;

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @Mock
    private Path path;

    @Test
    void handleValidationExceptions_shouldReturnValidationErrors() {
        // 1. Создаем реальный FieldError
        FieldError fieldError = new FieldError("object", "amount", "must be positive");

        // 2. Мокаем только BindingResult
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        // 3. Создаем реальное исключение с моком BindingResult
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // 4. Вызываем и проверяем
        ResponseEntity<?> response = exceptionHandler.handleValidationExceptions(ex);

        // 3. Проверки
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        // 4. Проверка через toString() (как временное решение)
        String responseBody = response.getBody().toString();
        assertTrue(responseBody.contains("message=Validation failed"));
        assertTrue(responseBody.contains("amount=must be positive"));
    }

    @Test
    void handleConstraintViolation_shouldReturnConstraintErrors() {
        // Arrange
        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("transferAmount");
        when(constraintViolation.getMessage()).thenReturn("must be less than 1000000");
        when(constraintViolationException.getConstraintViolations())
                .thenReturn(Set.of(constraintViolation));

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleConstraintViolation(constraintViolationException);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Constraint violation", response.getBody().message());
        assertEquals("must be less than 1000000", response.getBody().errors().get("transferAmount"));
    }

    @Test
    void handleInvalidJson_shouldReturnJsonError() {
        // Arrange
        when(httpMessageNotReadableException.getMessage()).thenReturn("Invalid JSON");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleInvalidJson(httpMessageNotReadableException);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid JSON format", response.getBody().message());
        assertEquals("Malformed JSON request", response.getBody().errors().get("error"));
    }

    @Test
    void handleEntityNotFound_shouldReturnNotFoundError() {
        // Arrange
        when(entityNotFoundException.getMessage()).thenReturn("Account not found");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleEntityNotFound(entityNotFoundException);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody().message());
        assertEquals("Account not found", response.getBody().errors().get("error"));
    }

    @Test
    void handleAllExceptions_shouldReturnInternalServerError() {
        // Arrange
        when(genericException.getMessage()).thenReturn("Unexpected error");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/accounts");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleAllExceptions(genericException, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().message());
        assertEquals("uri=/api/accounts", response.getBody().errors().get("path"));
    }

    @Test
    void errorResponse_shouldCorrectlyStoreValues() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error message");

        // Act
        var errorResponse = new GlobalExceptionHandler.ErrorResponse(now, 400, "Bad request", errors);

        // Assert
        assertEquals(now, errorResponse.localDateTime());
        assertEquals(400, errorResponse.status());
        assertEquals("Bad request", errorResponse.message());
        assertEquals("error message", errorResponse.errors().get("field"));
    }
}
