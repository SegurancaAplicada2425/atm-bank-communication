package com.atmbank.bank.security;

import java.security.Key;
import java.security.KeyPair;

public class SecurityContext {
    private final byte[] secret;
    private final Key key;
    private final KeyPair keyPair;

    public SecurityContext(byte[] secret, Key key, KeyPair keyPair) {
        this.secret = secret;
        this.key = key;
        this.keyPair = keyPair;
    }

    public byte[] getSecret() {
        return secret;
    }

    public Key getKey() {
        return key;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
