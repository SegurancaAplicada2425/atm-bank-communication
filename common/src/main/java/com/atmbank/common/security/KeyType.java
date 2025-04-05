package com.atmbank.common.security;

public enum KeyType {
    SYMMETRIC,
    PRIVATE,
    PUBLIC;

    @Override
    public String toString() {
        return name();
    }
}
