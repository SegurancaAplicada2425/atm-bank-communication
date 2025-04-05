package com.atmbank.common.message.response;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class HandshakeResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    public HandshakeResponse(ResponseStatus status) {
        super(MessageType.HANDSHAKE, status);
    }
}
