package com.atmbank.common.message.request;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class WithdrawRequest extends OperationRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double amount;

    public WithdrawRequest(String accountNumber, String accountPin, double amount) {
        super(MessageType.WITHDRAW, accountNumber, accountPin);
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("WithdrawRequest{type='%s', accountNumber='%s', accountPin='%s', amount=%.2f}", getType(), getAccountNumber(), getAccountPin(), amount);
    }
}
