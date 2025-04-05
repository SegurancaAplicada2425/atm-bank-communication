package com.atmbank.common.message.response;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class GetBalanceResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Double balance;

    public GetBalanceResponse(double balance) {
        super(MessageType.GET_BALANCE, ResponseStatus.SUCCESS);
        this.balance = balance;
    }

    public GetBalanceResponse(ResponseStatus status) {
        super(MessageType.GET_BALANCE, status);
        this.balance = null;
    }

    public Double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        if (getStatus() != ResponseStatus.SUCCESS) {
            return super.toString();
        }
        return String.format("GetBalanceResponse{type='%s', balance=%.2f}", getType(), balance);
    }
}
