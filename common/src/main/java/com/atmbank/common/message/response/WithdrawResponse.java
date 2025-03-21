package com.atmbank.common.message.response;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class WithdrawResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    public WithdrawResponse(ResponseStatus status) {
        super(MessageType.WITHDRAW, status);
    }
}
