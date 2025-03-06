package com.bank.authorization.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AuthResponse {
    private Map<String, Object> data;

    public AuthResponse(Map<String, Object> data) {
        this.data = data;
    }
}
