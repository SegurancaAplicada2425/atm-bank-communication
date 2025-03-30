package com.atmbank.common.security;

import java.security.Key;
import java.security.PublicKey;

import org.apache.commons.codec.binary.Hex;

public class AuthFileData {
    private final byte[] secret;
    private final Key key;
    private final PublicKey bankPublicKey;

    public AuthFileData(byte[] secret, Key key, PublicKey bankPublicKey) {
        this.secret = secret;
        this.key = key;
        this.bankPublicKey = bankPublicKey;
    }

    public byte[] getSecret() {
        return secret;
    }

    public Key getKey() {
        return key;
    }

    public PublicKey getBankPublicKey() {
        return bankPublicKey;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Hex.encodeHexString(secret)).append('\n');
        builder.append(AESUtils.getKeyHex(key)).append('\n');
        builder.append(RSAUtils.getPublicKeyHex(bankPublicKey));
        return builder.toString();
    }
}
