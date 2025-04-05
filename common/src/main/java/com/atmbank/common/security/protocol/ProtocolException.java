package com.atmbank.common.security.protocol;

import com.atmbank.common.security.SecurityException;

import java.io.Serial;

public class ProtocolException extends SecurityException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ProtocolException() {
        super("Protocol error occurred");
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}
