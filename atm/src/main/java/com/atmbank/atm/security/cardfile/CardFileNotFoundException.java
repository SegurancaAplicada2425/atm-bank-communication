package com.atmbank.atm.security.cardfile;

import java.io.Serial;

public class CardFileNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public CardFileNotFoundException() {
        super("Card file doesn't exists");
    }

    public CardFileNotFoundException(String message) {
        super(message);
    }

    public CardFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardFileNotFoundException(Throwable cause) {
        super(cause);
    }
}
