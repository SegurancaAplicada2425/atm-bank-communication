package com.atmbank.atm.security.cardfile;

import java.io.Serial;

public class CardFileAlreadyExistsException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public CardFileAlreadyExistsException() {
        super("Card file already exists");
    }

    public CardFileAlreadyExistsException(String message) {
        super(message);
    }

    public CardFileAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardFileAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
