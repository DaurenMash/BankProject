package com.bank.publicinfo.exception;

import lombok.Getter;

@Getter
public class SecurityException extends java.lang.SecurityException {

    public SecurityException(String message) {
        super(message);

    }

}
