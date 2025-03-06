package com.bank.authorization.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        final SecureRandom secureRandom = new SecureRandom();
        final int keySize =  32; // Рекомендуемый размер ключа для HS256 — 256 бит (32 байта)
        final byte[] key = new byte[keySize];
        secureRandom.nextBytes(key);
        final String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println(encodedKey);
    }
}
