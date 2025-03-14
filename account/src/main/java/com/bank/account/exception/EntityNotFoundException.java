package com.bank.account.exception;

public class EntityNotFoundException extends jakarta.persistence.EntityNotFoundException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
