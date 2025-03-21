package com.atmbank.common.logger;

public interface Logger {
    void info(String message);

    void info(String message, Object... args);

    void error(String message);

    void error(String message, Object... args);
}
