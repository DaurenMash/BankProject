package com.bank.authorization.dto;

import lombok.Data;

@Data
public class KafkaRequest {
    private String jwtToken;
    private Object payload;
}