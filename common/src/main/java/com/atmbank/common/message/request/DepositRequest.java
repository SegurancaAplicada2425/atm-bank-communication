package com.atmbank.common.message.request;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class DepositRequest extends OperationRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double amount;

    public DepositRequest(String accountNumber, String accountPin, double amount) {
        super(MessageType.DEPOSIT, accountNumber, accountPin);
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("DepositRequest{type='%s', accountNumber='%s', accountPin='%s', amount=%.2f}", getType(), getAccountNumber(), getAccountPin(), amount);
    }
}
