package com.atmbank.common.display;

public interface Display {
    void display(String message);

    void display(String message, Object... args);
}
