package com.atmbank.common.message;

public enum MessageType {
    HANDSHAKE,
    CREATE_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    GET_BALANCE,
    ERROR;

    @Override
    public String toString() {
        return name();
    }
}
