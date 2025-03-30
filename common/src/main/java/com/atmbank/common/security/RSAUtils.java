package com.atmbank.common.security;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class RSAUtils {
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] getPublicKeyBytes(PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    public static byte[] getPublicKeyBytes(KeyPair keyPair) {
        return getPublicKeyBytes(keyPair.getPublic());
    }

    public static byte[] getPrivateKeyBytes(PrivateKey privateKey) {
        return privateKey.getEncoded();
    }

    public static byte[] getPrivateKeyBytes(KeyPair keyPair) {
        return getPrivateKeyBytes(keyPair.getPrivate());
    }

    public static String getPublicKeyHex(PublicKey publicKey) {
        return Hex.encodeHexString(getPublicKeyBytes(publicKey));
    }

    public static String getPublicKeyHex(KeyPair keyPair) {
        return getPublicKeyHex(keyPair.getPublic());
    }

    public static String getPrivateKeyHex(PrivateKey privateKey) {
        return Hex.encodeHexString(getPrivateKeyBytes(privateKey));
    }

    public static String getPrivateKeyHex(KeyPair keyPair) {
        return getPrivateKeyHex(keyPair.getPrivate());
    }

    public static PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec keySpec = new X509EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey getPublicKeyFromHex(String publicKeyHex)
            throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        byte[] publicKeyBytes = Hex.decodeHex(publicKeyHex);
        return getPublicKeyFromBytes(publicKeyBytes);
    }

    public static PrivateKey getPrivateKeyFromHex(String privateKeyHex)
            throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        byte[] privateKeyBytes = Hex.decodeHex(privateKeyHex);
        return getPrivateKeyFromBytes(privateKeyBytes);
    }

    public static byte[] encrypt(Key key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(Key key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }
}
