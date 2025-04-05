package com.atmbank.common.logger;

public class ConditionalLogger implements Logger {
    private final Logger logger;
    private final boolean enabled;

    public ConditionalLogger(boolean enabled) {
        this.enabled = enabled;
        this.logger = enabled ? new ConsoleLogger() : new NullLogger();
    }

    public ConditionalLogger(boolean enabled, Logger logger) {
        this.enabled = enabled;
        this.logger = enabled ? logger : new NullLogger();
    }

    @Override
    public void info(String message) {
        if (enabled) {
            logger.info(message);
        }
    }

    @Override
    public void info(String message, Object... args) {
        if (enabled) {
            logger.info(message, args);
        }
    }

    @Override
    public void error(String message) {
        if (enabled) {
            logger.error(message);
        }
    }

    @Override
    public void error(String message, Object... args) {
        if (enabled) {
            logger.error(message, args);
        }
    }
}
