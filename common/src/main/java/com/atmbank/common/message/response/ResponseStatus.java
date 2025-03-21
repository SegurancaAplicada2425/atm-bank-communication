package com.atmbank.common.message.response;

public enum ResponseStatus {
    SUCCESS,
    AUTHENTICATION_REQUIRED,
    ACCOUNT_EXISTS,
    ACCOUNT_NOT_FOUND,
    NEGATIVE_AMOUNT,
    NEGATIVE_OR_ZERO_AMOUNT,
    INSUFFICIENT_BALANCE,
    UNKNOWN_REQUEST,
    ERROR;

    @Override
    public String toString() {
        return name();
    }
}
