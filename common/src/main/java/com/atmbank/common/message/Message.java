package com.atmbank.common.message;

import java.io.Serial;
import java.io.Serializable;

public abstract class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final MessageType type;

    protected Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s{type='%s'}", getClass().getSimpleName(), type);
    }
}
