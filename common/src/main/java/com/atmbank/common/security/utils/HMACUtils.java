package com.atmbank.common.security.utils;

import com.atmbank.common.config.Constants;
import com.atmbank.common.utils.ConversionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;

public class HMACUtils {
    private static final String ALGORITHM = Constants.HMAC_ALGORITHM;

    public static byte[] generate(byte[] keyBytes, byte[] data) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    public static byte[] generate(Key key, byte[] data) throws Exception {
        byte[] keyBytes = key.getEncoded();
        return generate(keyBytes, data);
    }

    public static byte[] generate(String keyHex, byte[] data) throws Exception {
        byte[] keyBytes = ConversionUtils.toBytes(keyHex, true);
        return generate(keyBytes, data);
    }

    public static String generateHex(byte[] keyBytes, byte[] data) throws Exception {
        byte[] hmacData = generate(data, keyBytes);
        return ConversionUtils.toHexString(hmacData);
    }

    public static String generateHex(Key key, byte[] data) throws Exception {
        byte[] keyBytes = key.getEncoded();
        return generateHex(keyBytes, data);
    }

    public static String generateHex(String keyHex, byte[] data) throws Exception {
        byte[] keyBytes = ConversionUtils.toBytes(keyHex, true);
        return generateHex(keyBytes, data);
    }

    public static boolean verify(byte[] keyBytes, byte[] data, byte[] hmac) throws Exception {
        byte[] generatedHMAC = generate(keyBytes, data);
        return Arrays.equals(generatedHMAC, hmac);
    }

    public static boolean verify(Key key, byte[] data, byte[] hmac) throws Exception {
        byte[] generatedHMAC = generate(key, data);
        return Arrays.equals(generatedHMAC, hmac);
    }

    public static boolean verify(String keyHex, byte[] data, byte[] hmac) throws Exception {
        byte[] generatedHMAC = generate(keyHex, data);
        return Arrays.equals(generatedHMAC, hmac);
    }

    public static boolean verify(byte[] keyBytes, byte[] data, String hmacHex) throws Exception {
        byte[] hmac = ConversionUtils.toBytes(hmacHex, true);
        return verify(keyBytes, data, hmac);
    }

    public static boolean verify(Key key, byte[] data, String hmacHex) throws Exception {
        byte[] hmac = ConversionUtils.toBytes(hmacHex, true);
        return verify(key, data, hmac);
    }

    public static boolean verify(String keyHex, byte[] data, String hmacHex) throws Exception {
        byte[] hmac = ConversionUtils.toBytes(hmacHex, true);
        return verify(keyHex, data, hmac);
    }
}
