package com.atmbank.bank.security.authfile;

import java.io.Serial;

public class AuthFileAlreadyExistsException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public AuthFileAlreadyExistsException() {
        super("Auth file already exists");
    }

    public AuthFileAlreadyExistsException(String message) {
        super(message);
    }

    public AuthFileAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthFileAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
