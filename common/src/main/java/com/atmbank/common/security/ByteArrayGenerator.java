package com.atmbank.common.security;

import java.security.SecureRandom;

public class ByteArrayGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] generate(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }
}
