package com.atmbank.common.security.utils;

import com.atmbank.common.config.Constants;
import com.atmbank.common.utils.ConversionUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtils {
    private static final String KEY_ALGORITHM = Constants.RSA_KEY_ALGORITHM;
    private static final int KEY_SIZE = Constants.RSA_KEY_SIZE;
    private static final String ALGORITHM = Constants.RSA_ALGORITHM;
    private static final String SIGNATURE_ALGORITHM = Constants.RSA_SIGNATURE_ALGORITHM;

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    public static PrivateKey getPrivateKey(KeyPair keyPair) {
        return keyPair.getPrivate();
    }

    public static PublicKey getPublicKey(KeyPair keyPair) {
        return keyPair.getPublic();
    }

    public static byte[] getPrivateKeyBytes(PrivateKey privateKey) {
        return privateKey.getEncoded();
    }

    public static byte[] getPublicKeyBytes(PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    public static byte[] getPrivateKeyBytes(KeyPair keyPair) {
        return getPrivateKeyBytes(getPrivateKey(keyPair));
    }

    public static byte[] getPublicKeyBytes(KeyPair keyPair) {
        return getPublicKeyBytes(getPublicKey(keyPair));
    }

    public static String getPrivateKeyHex(PrivateKey privateKey) {
        return ConversionUtils.toHexString(getPrivateKeyBytes(privateKey));
    }

    public static String getPublicKeyHex(PublicKey publicKey) {
        return ConversionUtils.toHexString(getPublicKeyBytes(publicKey));
    }

    public static String getPrivateKeyHex(KeyPair keyPair) {
        return getPrivateKeyHex(getPrivateKey(keyPair));
    }

    public static String getPublicKeyHex(KeyPair keyPair) {
        return getPublicKeyHex(getPublicKey(keyPair));
    }

    public static String getPrivateKeyHex(byte[] privateKey) {
        return ConversionUtils.toHexString(privateKey);
    }

    public static String getPublicKeyHex(byte[] publicKey) {
        return ConversionUtils.toHexString(publicKey);
    }

    public static PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes) throws Exception {
        EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes) throws Exception {
        EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKeyFromHex(String privateKeyHex) throws Exception {
        byte[] privateKeyBytes = ConversionUtils.toBytes(privateKeyHex, true);
        return getPrivateKeyFromBytes(privateKeyBytes);
    }

    public static PublicKey getPublicKeyFromHex(String publicKeyHex) throws Exception {
        byte[] publicKeyBytes = ConversionUtils.toBytes(publicKeyHex, true);
        return getPublicKeyFromBytes(publicKeyBytes);
    }

    public static byte[] encryptWithPrivateKey(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] encryptWithPrivateKey(byte[] privateKeyBytes, byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromBytes(privateKeyBytes);
        return encryptWithPrivateKey(privateKey, data);
    }

    public static byte[] encryptWithPrivateKey(String privateKeyHex, byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromHex(privateKeyHex);
        return encryptWithPrivateKey(privateKey, data);
    }

    public static byte[] decryptWithPublicKey(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptWithPublicKey(byte[] publicKeyBytes, byte[] data) throws Exception {
        PublicKey publicKey = getPublicKeyFromBytes(publicKeyBytes);
        return decryptWithPublicKey(publicKey, data);
    }

    public static byte[] decryptWithPublicKey(String publicKeyHex, byte[] data) throws Exception {
        PublicKey publicKey = getPublicKeyFromHex(publicKeyHex);
        return decryptWithPublicKey(publicKey, data);
    }

    public static byte[] encryptWithPublicKey(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] encryptWithPublicKey(byte[] publicKeyBytes, byte[] data) throws Exception {
        PublicKey publicKey = getPublicKeyFromBytes(publicKeyBytes);
        return encryptWithPublicKey(publicKey, data);
    }

    public static byte[] encryptWithPublicKey(String publicKeyHex, byte[] data) throws Exception {
        PublicKey publicKey = getPublicKeyFromHex(publicKeyHex);
        return encryptWithPublicKey(publicKey, data);
    }

    public static byte[] decryptWithPrivateKey(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptWithPrivateKey(byte[] privateKeyBytes, byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromBytes(privateKeyBytes);
        return decryptWithPrivateKey(privateKey, data);
    }

    public static byte[] decryptWithPrivateKey(String privateKeyHex, byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromHex(privateKeyHex);
        return decryptWithPrivateKey(privateKey, data);
    }

    public static byte[] sign(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public static byte[] sign(byte[] privateKeyBytes, byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromBytes(privateKeyBytes);
        return sign(privateKey, data);
    }

    public static byte[] sign(String privateKeyHex, byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromHex(privateKeyHex);
        return sign(privateKey, data);
    }

    public static boolean verify(PublicKey publicKey, byte[] data, byte[] signatureBytes) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    public static boolean verify(byte[] publicKeyBytes, byte[] data, byte[] signatureBytes) throws Exception {
        PublicKey publicKey = getPublicKeyFromBytes(publicKeyBytes);
        return verify(publicKey, data, signatureBytes);
    }

    public static boolean verify(String publicKeyHex, byte[] data, byte[] signatureBytes) throws Exception {
        PublicKey publicKey = getPublicKeyFromHex(publicKeyHex);
        return verify(publicKey, data, signatureBytes);
    }
}
