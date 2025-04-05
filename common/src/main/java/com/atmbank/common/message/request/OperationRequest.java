package com.atmbank.common.message.request;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public abstract class OperationRequest extends Request {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final String accountPin;

    public OperationRequest(MessageType type, String accountNumber, String accountPin) {
        super(type);
        this.accountNumber = accountNumber;
        this.accountPin = accountPin;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountPin() {
        return accountPin;
    }

    @Override
    public String toString() {
        return String.format("%s{type='%s', accountNumber='%s', accountPin='%s'}", getClass().getSimpleName(), getType(), accountNumber, accountPin);
    }
}
