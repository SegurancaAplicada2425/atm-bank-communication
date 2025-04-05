package com.atmbank.common.message.request;

import com.atmbank.common.config.Constants;
import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class CreateAccountRequest extends OperationRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double initialBalance;

    public CreateAccountRequest(String accountNumber, String accountPin, double initialBalance) {
        super(MessageType.CREATE_ACCOUNT, accountNumber, accountPin);
        if (initialBalance < Constants.MIN_ACCOUNT_INITIAL_BALANCE) {
            throw new IllegalArgumentException("Initial balance must be at least " + Constants.MIN_ACCOUNT_INITIAL_BALANCE);
        }
        this.initialBalance = initialBalance;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    @Override
    public String toString() {
        return String.format("CreateAccountRequest{type='%s', accountNumber='%s', accountPin='%s', initialBalance=%.2f}", getType(), getAccountNumber(), getAccountPin(), initialBalance);
    }
}
