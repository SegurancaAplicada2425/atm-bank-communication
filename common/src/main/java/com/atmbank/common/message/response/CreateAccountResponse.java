package com.atmbank.common.message.response;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class CreateAccountResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    public CreateAccountResponse(ResponseStatus status) {
        super(MessageType.CREATE_ACCOUNT, status);
    }
}
