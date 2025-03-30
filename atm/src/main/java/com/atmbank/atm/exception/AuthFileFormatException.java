package com.atmbank.atm.exception;

import java.io.Serial;

public class AuthFileFormatException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public AuthFileFormatException() {
        super("Invalid auth file format.");
    }

    public AuthFileFormatException(String message) {
        super(message);
    }

    public AuthFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthFileFormatException(Throwable cause) {
        super(cause);
    }
}
