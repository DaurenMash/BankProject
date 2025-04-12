package com.bank.profile.exception;

import jakarta.persistence.EntityExistsException;

public class EntityNotUniqueException extends EntityExistsException {
    public final String className;
    public final String fieldName;

    public EntityNotUniqueException(String className, String fieldName)
    {
        super();

        this.className = className;
        this.fieldName = fieldName;
    }
}
