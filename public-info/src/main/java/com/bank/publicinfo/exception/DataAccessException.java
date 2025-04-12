package com.bank.publicinfo.exception;

public class DataAccessException extends org.springframework.dao.DataAccessException {

    public DataAccessException(String message) {
        super(message);
    }

}
