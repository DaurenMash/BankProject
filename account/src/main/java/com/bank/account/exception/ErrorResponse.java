package com.bank.account.exception;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ErrorResponse {
    private String errorCode;
    private String message;
    private Timestamp timestamp;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
