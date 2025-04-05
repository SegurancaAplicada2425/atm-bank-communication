package com.atmbank.common.message.response;

public enum ResponseStatus {
    SUCCESS,
    AUTHENTICATION_REQUIRED,
    ACCOUNT_EXISTS,
    ACCOUNT_NOT_FOUND,
    INVALID_AMOUNT,
    INSUFFICIENT_BALANCE,
    UNKNOWN_REQUEST,
    ERROR;

    @Override
    public String toString() {
        return name();
    }
}
