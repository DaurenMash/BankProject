package com.bank.publicinfo.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class CustomJsonProcessingException extends JsonProcessingException {

    public CustomJsonProcessingException(String message) {
        super(message);
    }

}
