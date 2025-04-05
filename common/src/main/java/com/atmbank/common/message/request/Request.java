package com.atmbank.common.message.request;

import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;

import java.io.Serial;

public abstract class Request extends Message {
    @Serial
    private static final long serialVersionUID = 1L;

    protected Request(MessageType type) {
        super(type);
    }
}
