package com.atmbank.common.message.response;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class ErrorResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public ErrorResponse(String errorMessage) {
        super(MessageType.ERROR, ResponseStatus.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return String.format("ErrorResponse{type='%s', errorMessage='%s'}", getType(), errorMessage);
    }
}
