package com.atmbank.bank.security;

public class SecurityContext {
    private final byte[] bankPrivateKey;
    private final byte[] bankPublicKey;
    private byte[] atmKey;

    public SecurityContext(byte[] bankPrivateKey, byte[] bankPublicKey) {
        this.bankPrivateKey = bankPrivateKey;
        this.bankPublicKey = bankPublicKey;
    }

    public byte[] getBankPrivateKey() {
        return bankPrivateKey;
    }

    public byte[] getBankPublicKey() {
        return bankPublicKey;
    }

    public byte[] getAtmKey() {
        return atmKey;
    }

    public void setAtmKey(byte[] atmKey) {
        this.atmKey = atmKey;
    }
}
