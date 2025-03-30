package com.atmbank.common.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class HMACUtils {
    private static final String ALGORITHM = "HMACSHA256";

    public static byte[] generateHMAC(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        mac.init(secretKeySpec);
        byte[] hmacData = mac.doFinal(data);
        return hmacData;
    }

    public static String generateHMACHex(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] hmacData = generateHMAC(data, key);
        return Hex.encodeHexString(hmacData);
    }

    public static boolean verifyHMAC(byte[] data, byte[] key, byte[] hmac)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] generatedHMAC = generateHMAC(data, key);
        return java.util.Arrays.equals(generatedHMAC, hmac);
    }
}
