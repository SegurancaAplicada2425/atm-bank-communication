package com.atmbank.common.message.request;

import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class WithdrawRequest extends Message {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final double amount;

    public WithdrawRequest(String accountNumber, double amount) {
        super(MessageType.WITHDRAW);
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
        return String.format("WithdrawRequest{type='%s', accountNumber='%s', amount=%.2f}",
                getType(), accountNumber, amount);
    }
}
