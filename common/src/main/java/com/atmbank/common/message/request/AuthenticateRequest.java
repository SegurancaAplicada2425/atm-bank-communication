package com.atmbank.common.message.request;

import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class AuthenticateRequest extends Message {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String credentials;

    public AuthenticateRequest(String credentials) {
        super(MessageType.AUTHENTICATE);
        this.credentials = credentials;
    }

    public String getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return String.format("AuthenticateRequest{type='%s', credentials='%s'}", getType(), credentials);
    }
}
