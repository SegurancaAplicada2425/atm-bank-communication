package com.atmbank.common.message.request;

import java.io.Serial;

import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;

public class CreateAccountRequest extends Message {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final double initialBalance;

    public CreateAccountRequest(String accountNumber, double initialBalance) {
        super(MessageType.CREATE_ACCOUNT);
        this.accountNumber = accountNumber;
        this.initialBalance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    @Override
    public String toString() {
        return String.format("CreateAccountRequest{type='%s', accountNumber='%s', initialBalance=%.2f}", getType(),
                accountNumber, initialBalance);
    }
}
