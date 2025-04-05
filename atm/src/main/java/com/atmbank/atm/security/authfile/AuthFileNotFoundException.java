package com.atmbank.atm.security.authfile;

import java.io.Serial;

public class AuthFileNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public AuthFileNotFoundException() {
        super("Auth file doesn't exists");
    }

    public AuthFileNotFoundException(String message) {
        super(message);
    }

    public AuthFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthFileNotFoundException(Throwable cause) {
        super(cause);
    }
}
