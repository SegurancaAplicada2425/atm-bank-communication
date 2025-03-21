package com.atmbank.common.logger;

public class NullLogger implements Logger {
    public void info(String message) {
    }

    public void info(String message, Object... args) {
    }

    public void error(String message) {
    }

    public void error(String message, Object... args) {
    }
}
