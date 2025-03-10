package com.bank.authorization.controller;

import com.bank.authorization.dto.ErrorDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Класс для глобальной обработки исключений в API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Метод для обработки EntityNotFoundException.
     *
     * @param ex Исключение EntityNotFoundException
     * @return Структурированный ответ с информацией об ошибке
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorDetails handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ErrorDetails(new Date(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    /**
     * Метод для обработки ValidationException.
     *
     * @param ex Исключение ValidationException
     * @return Структурированный ответ с информацией об ошибке
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorDetails handleValidationException(ValidationException ex) {
        return new ErrorDetails(new Date(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * Метод для обработки ConstraintViolationException (в случае нарушений ограничений).
     *
     * @param ex Исключение ConstraintViolationException
     * @return Структурированный ответ с информацией об ошибке
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDetails handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        return new ErrorDetails(new Date(), HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * Метод для обработки IllegalArgumentException.
     *
     * @param ex Исключение IllegalArgumentException
     * @return Структурированный ответ с информацией об ошибке
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorDetails handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorDetails(new Date(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * Метод для обработки всех остальных исключений.
     *
     * @param ex Любое другое исключение
     * @return Структурированный ответ с информацией об ошибке
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDetails handleAllExceptions(Exception ex) {
        return new ErrorDetails(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
}