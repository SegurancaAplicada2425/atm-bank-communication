package com.atmbank.common.security.utils;

import com.atmbank.common.config.Constants;

import java.security.MessageDigest;
import java.util.Arrays;

public class SecretReducerUtils {
    private static final String ALGORITHM = Constants.SECRET_REDUCER_ALGORITHM;

    public static byte[] reduce(byte[] secret, int length) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        byte[] hash = digest.digest(secret);
        return Arrays.copyOf(hash, length / 8);
    }
}
