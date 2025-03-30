package com.atmbank.common.message.response;

import java.io.Serial;

import com.atmbank.common.message.MessageType;

public class WithdrawResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    public WithdrawResponse(ResponseStatus status) {
        super(MessageType.WITHDRAW, status);
    }
}
