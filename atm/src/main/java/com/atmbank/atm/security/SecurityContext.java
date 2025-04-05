package com.atmbank.atm.security;

public class SecurityContext {
    private final byte[] atmKey;
    private final byte[] bankPublicKey;

    public SecurityContext(byte[] atmKey, byte[] bankPublicKey) {
        this.atmKey = atmKey;
        this.bankPublicKey = bankPublicKey;
    }

    public byte[] getAtmKey() {
        return atmKey;
    }

    public byte[] getBankPublicKey() {
        return bankPublicKey;
    }
}
