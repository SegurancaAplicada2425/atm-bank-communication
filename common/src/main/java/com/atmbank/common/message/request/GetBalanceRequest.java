package com.atmbank.common.message.request;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class GetBalanceRequest extends OperationRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    public GetBalanceRequest(String accountNumber, String accountPin) {
        super(MessageType.GET_BALANCE, accountNumber, accountPin);
    }

    @Override
    public String toString() {
        return String.format("GetBalanceRequest{type='%s', accountNumber='%s', accountPin='%s'}", getType(), getAccountNumber(), getAccountPin());
    }
}
