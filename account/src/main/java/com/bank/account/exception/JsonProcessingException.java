package com.bank.account.exception;

public class JsonProcessingException extends com.fasterxml.jackson.core.JsonProcessingException {
    public JsonProcessingException(String message) {
        super(message);
    }
}
