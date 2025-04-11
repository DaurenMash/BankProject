package com.bank.profile.handler;

import com.bank.profile.dto.ErrorDto;
import com.bank.profile.exception.EntityNotUniqueException;
import com.bank.profile.kafka.producer.ErrorProducer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class TestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorProducer errorProducer;

    @ExceptionHandler(value = { EntityNotFoundException.class, })
    protected void handleNotFound(RuntimeException ex) {
        log.warn(ex.getMessage());
        errorProducer.sendError(ErrorDto.of(ex.getMessage()));
    }

    @ExceptionHandler(value = {EntityNotUniqueException.class })
    protected void handleNotUnique(EntityNotUniqueException ex) {
        String message = ex.className + " with the same " + ex.fieldName + " already exists";
        log.warn(message);
        errorProducer.sendError(ErrorDto.of(message));
    }
}
