package com.bank.profile.handler;

import com.bank.profile.dto.ErrorDto;
import com.bank.profile.exception.EntityNotUniqueException;
import com.bank.profile.kafka.producer.ErrorProducer;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@ControllerAdvice
public class KafkaResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private final ErrorProducer errorProducer;

    public KafkaResponseEntityExceptionHandler(ErrorProducer errorProducer) {
        this.errorProducer = errorProducer;
    }

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

    @ExceptionHandler(value = {ListenerExecutionFailedException.class })
    protected void handleKafkaListenerFail(ListenerExecutionFailedException ex) {
        String message = ex.getMessage();
        log.warn(message);
        errorProducer.sendError(ErrorDto.of(message));
    }

    @ExceptionHandler(value = {KafkaException.class })
    protected void handleGeneralKafkaFail(KafkaException ex) {
        String message = ex.getMessage();
        log.warn(message);
        errorProducer.sendError(ErrorDto.of(message));
    }
}
