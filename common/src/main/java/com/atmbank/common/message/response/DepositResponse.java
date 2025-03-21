package com.atmbank.common.message.response;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class DepositResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    public DepositResponse(ResponseStatus status) {
        super(MessageType.DEPOSIT, status);
    }
}
