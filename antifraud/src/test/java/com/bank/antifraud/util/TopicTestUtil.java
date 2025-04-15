package com.bank.antifraud.util;

import lombok.Getter;

@Getter
public enum TopicTestUtil {
    CREATE_TOPIC("suspicious-transfers.create"),
    UPDATE_TOPIC("suspicious-transfers.update"),
    DELETE_TOPIC("suspicious-transfers.delete"),
    GET_TOPIC("suspicious-transfers.get"),
    RESPONSE_TOPIC("suspicious-transfers.Response");


    private String type;

    TopicTestUtil(String type) {
        this.type = type;
    }
}
