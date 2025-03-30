package com.atmbank.atm.security;

import java.security.Key;
import java.security.PublicKey;

public class SecurityContext {
    private final byte[] secret;
    private final Key key;
    private final PublicKey bankPublicKey;

    public SecurityContext(byte[] secret, Key key, PublicKey serverPublicKey) {
        this.secret = secret;
        this.key = key;
        this.bankPublicKey = serverPublicKey;
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
}
