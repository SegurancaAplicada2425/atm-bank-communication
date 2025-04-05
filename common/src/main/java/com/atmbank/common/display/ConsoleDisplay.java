package com.atmbank.common.display;

public class ConsoleDisplay implements Display {
    @Override
    public void display(String message) {
        System.out.println(message);
        System.out.flush();
    }

    @Override
    public void display(String message, Object... args) {
        System.out.printf(message + "%n", args);
        System.out.flush();
    }
}
