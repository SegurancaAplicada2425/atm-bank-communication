package com.atmbank.common.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class AESUtils {
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final String ALGORITHM = "AES/CTR/NoPadding";

    public static Key generateKey() {
        byte[] keyBytes = ByteArrayGenerator.generate(KEY_SIZE / 8);
        SecretKey key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        return key;
    }

    public static byte[] getKeyBytes(Key key) {
        return key.getEncoded();
    }

    public static String getKeyHex(Key key) {
        return Hex.encodeHexString(getKeyBytes(key));
    }

    public static Key getKeyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public static Key getKeyFromHex(String keyHex) throws DecoderException {
        byte[] keyBytes = Hex.decodeHex(keyHex);
        return getKeyFromBytes(keyBytes);
    }

    public static byte[] encrypt(Key key, byte[] data) throws Exception {
        byte[] ivBytes = ByteArrayGenerator.generate(16);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] encryptedData = cipher.doFinal(data);

        byte[] result = new byte[ivBytes.length + encryptedData.length];
        System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
        System.arraycopy(encryptedData, 0, result, ivBytes.length, encryptedData.length);

        return result;
    }

    public static byte[] decrypt(Key key, byte[] data) throws Exception {
        byte[] ivBytes = new byte[16];
        System.arraycopy(data, 0, ivBytes, 0, ivBytes.length);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] encryptedData = new byte[data.length - ivBytes.length];
        System.arraycopy(data, ivBytes.length, encryptedData, 0, encryptedData.length);

        return cipher.doFinal(encryptedData);
    }
}
