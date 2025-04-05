package com.atmbank.common.message;

import java.io.Serial;

public class MessageFormatException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public MessageFormatException() {
        super("Message format exception");
    }

    public MessageFormatException(String message) {
        super(message);
    }

    public MessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageFormatException(Throwable cause) {
        super(cause);
    }
}
