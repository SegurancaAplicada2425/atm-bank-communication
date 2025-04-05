package com.atmbank.common.security.protocol;

public class SeqNumberManager {
    private int current;

    public SeqNumberManager() {
        current = 0;
    }

    public SeqNumberManager(int initial) {
        current = initial;
    }

    public void reset() {
        current = 0;
    }

    public void increment() {
        if (current == Integer.MAX_VALUE) {
            reset();
        } else {
            current++;
        }
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
