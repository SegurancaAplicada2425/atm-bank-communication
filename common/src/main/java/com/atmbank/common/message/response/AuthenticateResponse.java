package com.atmbank.common.message.response;

import com.atmbank.common.message.MessageType;

import java.io.Serial;

public class AuthenticateResponse extends Response {
    @Serial
    private static final long serialVersionUID = 1L;

    private final boolean authenticated;

    public AuthenticateResponse(boolean authenticated) {
        super(MessageType.AUTHENTICATE, authenticated ? ResponseStatus.SUCCESS : ResponseStatus.AUTHENTICATION_REQUIRED);
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public String toString() {
        return String.format("AuthenticateResponse{type='%s', status='%s', authenticated=%b}", getType(), getStatus(), authenticated);
    }
}
