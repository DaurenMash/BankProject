package com.bank.publicinfo.exception;

public class EntityNotFoundException extends jakarta.persistence.EntityNotFoundException {

    public EntityNotFoundException(String message) {
        super(message);
    }

}
