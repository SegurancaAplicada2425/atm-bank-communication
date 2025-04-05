package com.atmbank.common.security.message;

import com.atmbank.common.message.MessageFormatException;
import com.atmbank.common.message.StringMessageBuilder;
import com.atmbank.common.security.KeyType;
import com.atmbank.common.security.utils.AESUtils;
import com.atmbank.common.security.utils.HMACUtils;
import com.atmbank.common.security.utils.RSAUtils;
import com.atmbank.common.utils.ConversionUtils;

public class SecureStringMessageBuilder extends StringMessageBuilder {
    private final byte[] key;
    private final KeyType keyType;
    private final byte[] integrityKey;
    private final KeyType integrityKeyType;

    public SecureStringMessageBuilder(byte[] key) {
        super();
        this.key = key;
        this.keyType = KeyType.SYMMETRIC;
        this.integrityKey = key;
        this.integrityKeyType = KeyType.SYMMETRIC;
    }

    public SecureStringMessageBuilder(byte[] key, KeyType keyType) throws IllegalArgumentException {
        super();
        this.key = key;
        this.keyType = keyType;
        this.integrityKey = key;
        this.integrityKeyType = keyType;
        validate();
    }

    public SecureStringMessageBuilder(byte[] key, KeyType keyType, byte[] integrityKey, KeyType integrityKeyType) throws IllegalArgumentException {
        super();
        this.key = key;
        this.keyType = keyType;
        this.integrityKey = integrityKey;
        this.integrityKeyType = integrityKeyType;
        validate();
    }

    private void validate() throws IllegalArgumentException {
        if (integrityKeyType == KeyType.PUBLIC) {
            throw new IllegalArgumentException("Public integrity key is not supported");
        }
    }

    public String build() throws Exception {
        String message = super.build();
        byte[] messageBytes = ConversionUtils.toBytes(message);

        byte[] encryptedMessage = switch (keyType) {
            case SYMMETRIC -> AESUtils.encrypt(key, messageBytes);
            case PRIVATE -> RSAUtils.encryptWithPrivateKey(key, messageBytes);
            case PUBLIC -> RSAUtils.encryptWithPublicKey(key, messageBytes);
        };

        byte[] integrity = switch (integrityKeyType) {
            case SYMMETRIC -> HMACUtils.generate(integrityKey, encryptedMessage);
            case PRIVATE -> RSAUtils.sign(integrityKey, encryptedMessage);
            case PUBLIC -> throw new IllegalArgumentException("Public integrity key is not supported");
        };

        String encryptedMessageHex = ConversionUtils.toHexString(encryptedMessage);
        String integrityHex = ConversionUtils.toHexString(integrity);

        return "m" + KEY_VALUE_SEPARATOR + encryptedMessageHex + FIELD_SEPARATOR + "i" + KEY_VALUE_SEPARATOR + integrityHex;
    }

    public static SecureStringMessageBuilder from(byte[] key, KeyType keyType, byte[] integrityKey, KeyType integrityKeyType, String message) throws Exception {
        SecureStringMessageBuilder builder = new SecureStringMessageBuilder(key, keyType, integrityKey, integrityKeyType);

        String[] parts = message.split(FIELD_SEPARATOR_REGEX);
        if (parts.length != 2) {
            throw new MessageFormatException("Message does not contain two parts");
        }

        String[] encryptedMessageStrPair = parts[0].split(KEY_VALUE_SEPARATOR, 2);
        String[] integrityStrPair = parts[1].split(KEY_VALUE_SEPARATOR, 2);
        if (encryptedMessageStrPair.length != 2 || integrityStrPair.length != 2) {
            throw new MessageFormatException("Message does not contain key-value pairs");
        }

        String encryptedMessageStr = encryptedMessageStrPair[1];
        byte[] encryptedMessage = encryptedMessageStr != null && !encryptedMessageStr.isEmpty() ? ConversionUtils.toBytes(encryptedMessageStr, true) : null;

        String integrityStr = integrityStrPair[1];
        byte[] integrity = integrityStr != null && !integrityStr.isEmpty() ? ConversionUtils.toBytes(integrityStr, true) : null;

        if (encryptedMessage == null || integrity == null) {
            throw new MessageFormatException("Message does not contain encrypted message or integrity");
        }

        switch (integrityKeyType) {
            case SYMMETRIC -> {
                if (!HMACUtils.verify(integrityKey, encryptedMessage, integrity)) {
                    throw new SecurityException("HMAC verification failed");
                }
            }
            case PRIVATE -> {
                if (!RSAUtils.verify(integrityKey, encryptedMessage, integrity)) {
                    throw new SecurityException("Signature verification failed");
                }
            }
            case PUBLIC -> throw new IllegalArgumentException("Public integrity key is not supported");
        }

        byte[] decryptedMessage = switch (keyType) {
            case SYMMETRIC -> AESUtils.decrypt(key, encryptedMessage);
            case PRIVATE -> RSAUtils.decryptWithPrivateKey(key, encryptedMessage);
            case PUBLIC -> RSAUtils.decryptWithPublicKey(key, encryptedMessage);
        };

        String decryptedMessageStr = ConversionUtils.toString(decryptedMessage);
        String[] fieldPairs = decryptedMessageStr.split(FIELD_SEPARATOR_REGEX);

        for (String pair : fieldPairs) {
            String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue.length == 2) {
                builder.addField(keyValue[0], keyValue[1]);
            }
        }
        return builder;
    }

    public static SecureStringMessageBuilder from(byte[] key, KeyType keyType, String message) throws Exception {
        return from(key, keyType, key, keyType, message);
    }

    public static SecureStringMessageBuilder from(byte[] key, String message) throws Exception {
        return from(key, KeyType.SYMMETRIC, message);
    }

    public static SecureStringMessageBuilder from(byte[] key, KeyType keyType, String integrityKeyKey, KeyType integrityKeyType, String message) throws Exception {
        if (integrityKeyType == KeyType.PUBLIC) {
            throw new IllegalArgumentException("Public integrity key is not supported");
        }

        String[] parts = message.split(FIELD_SEPARATOR_REGEX);
        if (parts.length != 2) {
            throw new MessageFormatException("Message does not contain two parts");
        }

        String[] encryptedMessageStrPair = parts[0].split(KEY_VALUE_SEPARATOR, 2);
        String[] integrityStrPair = parts[1].split(KEY_VALUE_SEPARATOR, 2);
        if (encryptedMessageStrPair.length != 2 || integrityStrPair.length != 2) {
            throw new MessageFormatException("Message does not contain key-value pairs");
        }

        String encryptedMessageStr = encryptedMessageStrPair[1];
        byte[] encryptedMessage = encryptedMessageStr != null && !encryptedMessageStr.isEmpty() ? ConversionUtils.toBytes(encryptedMessageStr, true) : null;

        String integrityStr = integrityStrPair[1];
        byte[] integrity = integrityStr != null && !integrityStr.isEmpty() ? ConversionUtils.toBytes(integrityStr, true) : null;

        if (encryptedMessage == null || integrity == null) {
            throw new MessageFormatException("Message does not contain encrypted message or integrity");
        }

        byte[] decryptedMessage = switch (keyType) {
            case SYMMETRIC -> AESUtils.decrypt(key, encryptedMessage);
            case PRIVATE -> RSAUtils.decryptWithPrivateKey(key, encryptedMessage);
            case PUBLIC -> RSAUtils.decryptWithPublicKey(key, encryptedMessage);
        };

        String decryptedMessageStr = ConversionUtils.toString(decryptedMessage);

        String integrityKeyStr = null;
        String[] fieldPairs = decryptedMessageStr.split(FIELD_SEPARATOR_REGEX);
        for (String pair : fieldPairs) {
            String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue.length == 2 && keyValue[0].equals(integrityKeyKey)) {
                integrityKeyStr = keyValue[1];
                break;
            }
        }

        byte[] integrityKey = integrityKeyStr != null && !integrityKeyStr.isEmpty() ? ConversionUtils.toBytes(integrityKeyStr, true) : null;
        if (integrityKey == null) {
            throw new MessageFormatException("Integrity key is missing");
        }

        switch (integrityKeyType) {
            case SYMMETRIC -> {
                if (!HMACUtils.verify(integrityKey, encryptedMessage, integrity)) {
                    throw new SecurityException("HMAC verification failed");
                }
            }
            case PRIVATE -> {
                if (!RSAUtils.verify(integrityKey, encryptedMessage, integrity)) {
                    throw new SecurityException("Signature verification failed");
                }
            }
            case PUBLIC -> throw new IllegalArgumentException("Public integrity key is not supported");
        }

        SecureStringMessageBuilder builder = new SecureStringMessageBuilder(key, keyType, integrityKey, integrityKeyType);
        for (String pair : fieldPairs) {
            String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue.length == 2) {
                builder.addField(keyValue[0], keyValue[1]);
            }
        }
        return builder;
    }
}
