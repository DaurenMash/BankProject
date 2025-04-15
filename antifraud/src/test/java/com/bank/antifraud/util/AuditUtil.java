package com.bank.antifraud.util;

import lombok.Getter;

@Getter
public enum AuditUtil {
    CREATE_OPERATOR("CREATE"),
    UPDATE_OPERATOR("UPDATE"),
    TEST_TOPIC("test_topic");

    private String type;

    AuditUtil(String type) {
        this.type = type;
    }
}
