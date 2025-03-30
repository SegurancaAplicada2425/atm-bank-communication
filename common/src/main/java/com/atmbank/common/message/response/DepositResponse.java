package com.atmbank.common.message.response;

import java.io.Serial;

import com.atmbank.common.message.MessageType;

public class DepositResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    public DepositResponse(ResponseStatus status) {
        super(MessageType.DEPOSIT, status);
    }
}
