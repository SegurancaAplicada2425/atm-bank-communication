package com.atmbank.common.security.utils;

import com.atmbank.common.config.Constants;
import com.atmbank.common.utils.ConversionUtils;
import org.apache.commons.codec.DecoderException;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DHUtils {
    private static final String KEY_ALGORITHM = Constants.DH_KEY_ALGORITHM;
    private static final String ALGORITHM = Constants.DH_ALGORITHM;
    private static final int KEY_SIZE = Constants.DH_KEY_SIZE;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
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

    public static PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey getPublicKeyFromHex(String publicKeyHex) throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        byte[] publicKeyBytes = ConversionUtils.toBytes(publicKeyHex, true);
        return getPublicKeyFromBytes(publicKeyBytes);
    }

    public static PrivateKey getPrivateKeyFromHex(String privateKeyHex) throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException {
        byte[] privateKeyBytes = ConversionUtils.toBytes(privateKeyHex, true);
        return getPrivateKeyFromBytes(privateKeyBytes);
    }

    public static byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM);
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }

    public static byte[] generateSharedSecret(byte[] privateKeyBytes, byte[] publicKeyBytes) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromBytes(privateKeyBytes);
        PublicKey publicKey = getPublicKeyFromBytes(publicKeyBytes);
        return generateSharedSecret(privateKey, publicKey);
    }

    public static byte[] generateSharedSecret(String privateKeyHex, String publicKeyHex) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromHex(privateKeyHex);
        PublicKey publicKey = getPublicKeyFromHex(publicKeyHex);
        return generateSharedSecret(privateKey, publicKey);
    }

    public static byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey, int length) throws Exception {
        byte[] secret = generateSharedSecret(privateKey, publicKey);
        return SecretReducerUtils.reduce(secret, length);
    }

    public static byte[] generateSharedSecret(byte[] privateKeyBytes, byte[] publicKeyBytes, int length) throws Exception {
        byte[] secret = generateSharedSecret(privateKeyBytes, publicKeyBytes);
        return SecretReducerUtils.reduce(secret, length);
    }

    public static byte[] generateSharedSecret(String privateKeyHex, String publicKeyHex, int length) throws Exception {
        byte[] secret = generateSharedSecret(privateKeyHex, publicKeyHex);
        return SecretReducerUtils.reduce(secret, length);
    }
}
