package com.atmbank.common.message.request;

import java.io.Serial;

import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;

public class GetBalanceRequest extends Message {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String accountNumber;

    public GetBalanceRequest(String accountNumber) {
        super(MessageType.GET_BALANCE);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return String.format("GetBalanceRequest{type='%s', accountNumber='%s'}", getType(), accountNumber);
    }
}
