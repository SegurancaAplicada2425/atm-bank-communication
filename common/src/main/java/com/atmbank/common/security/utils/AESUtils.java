package com.atmbank.common.security.utils;

import com.atmbank.common.config.Constants;
import com.atmbank.common.utils.ByteArrayGenerator;
import com.atmbank.common.utils.ConversionUtils;
import org.apache.commons.codec.DecoderException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESUtils {
    private static final String KEY_ALGORITHM = Constants.AES_KEY_ALGORITHM;
    private static final int KEY_SIZE = Constants.AES_KEY_SIZE;
    private static final String ALGORITHM = Constants.AES_ALGORITHM;

    public static Key generateKey() {
        byte[] keyBytes = ByteArrayGenerator.generate(KEY_SIZE / 8);
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public static byte[] getKeyBytes(Key key) {
        return key.getEncoded();
    }

    public static String getKeyHex(Key key) {
        return ConversionUtils.toHexString(getKeyBytes(key));
    }

    public static Key getKeyFromBytes(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public static Key getKeyFromHex(String keyHex) throws DecoderException {
        byte[] keyBytes = ConversionUtils.toBytes(keyHex, true);
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

    public static byte[] encrypt(byte[] keyBytes, byte[] data) throws Exception {
        Key key = getKeyFromBytes(keyBytes);
        return encrypt(key, data);
    }

    public static byte[] encrypt(String keyHex, byte[] data) throws Exception {
        Key key = getKeyFromHex(keyHex);
        return encrypt(key, data);
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

    public static byte[] decrypt(byte[] keyBytes, byte[] data) throws Exception {
        Key key = getKeyFromBytes(keyBytes);
        return decrypt(key, data);
    }

    public static byte[] decrypt(String keyHex, byte[] data) throws Exception {
        Key key = getKeyFromHex(keyHex);
        return decrypt(key, data);
    }
}
