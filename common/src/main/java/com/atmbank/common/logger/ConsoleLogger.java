package com.atmbank.common.logger;

public class ConsoleLogger implements Logger {
    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void info(String message, Object... args) {
        System.out.printf(message + "%n", args);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }

    @Override
    public void error(String message, Object... args) {
        System.err.printf(message + "%n", args);
    }
}
