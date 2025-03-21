package com.atmbank.common.message.response;

import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;

import java.io.Serial;

public abstract class Response extends Message {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final ResponseStatus status;
    
    protected Response(MessageType type, ResponseStatus status) {
        super(type);
        this.status = status;
    }

    public boolean isSuccess() {
        return status == ResponseStatus.SUCCESS;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("%s{type='%s', status='%s'}", getClass().getSimpleName(), getType(), status);
    }
}
