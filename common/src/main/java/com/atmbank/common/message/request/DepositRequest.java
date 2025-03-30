package com.atmbank.common.message.request;

import java.io.Serial;

import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;

public class DepositRequest extends Message {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final double amount;

    public DepositRequest(String accountNumber, double amount) {
        super(MessageType.DEPOSIT);
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("DepositRequest{type='%s', accountNumber='%s', amount=%.2f}",
                getType(), accountNumber, amount);
    }
}
