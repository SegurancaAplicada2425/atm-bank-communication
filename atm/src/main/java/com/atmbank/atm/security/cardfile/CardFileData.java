package com.atmbank.atm.security.cardfile;

public class CardFileData {
    private final String accountPin;

    public CardFileData(String accountPin) {
        this.accountPin = accountPin;
    }

    public String getAccountPin() {
        return accountPin;
    }

    @Override
    public String toString() {
        return accountPin;
    }
}
