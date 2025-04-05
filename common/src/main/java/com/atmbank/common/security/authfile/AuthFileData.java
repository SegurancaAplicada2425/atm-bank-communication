package com.atmbank.common.security.authfile;

import com.atmbank.common.utils.ConversionUtils;

public class AuthFileData {
    private final byte[] bankPublicKey;

    public AuthFileData(byte[] bankPublicKey) {
        this.bankPublicKey = bankPublicKey;
    }

    public byte[] getBankPublicKey() {
        return bankPublicKey;
    }

    @Override
    public String toString() {
        return ConversionUtils.toHexString(bankPublicKey);
    }
}
