package com.atmbank.common.message;

public enum MessageType {
    AUTHENTICATE,
    CREATE_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    GET_BALANCE,
    UNKNOWN;

    @Override
    public String toString() {
        return name();
    }
}
