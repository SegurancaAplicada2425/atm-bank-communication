package com.atmbank.common.security;

import java.io.Serial;

public class SecurityException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public SecurityException() {
        super("Security error occurred");
    }

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }
}
