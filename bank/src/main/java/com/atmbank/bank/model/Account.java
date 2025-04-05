package com.atmbank.bank.model;

import com.atmbank.common.config.Constants;

public class Account {
    private final String accountNumber;
    private final String accountPin;
    private double balance;

    public Account(String accountNumber, String accountPin, double initialBalance) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        this.accountNumber = accountNumber;

        if (accountPin == null || accountPin.isEmpty()) {
            throw new IllegalArgumentException("Account PIN cannot be null or empty");
        }
        this.accountPin = accountPin;

        if (Double.isNaN(initialBalance)) {
            throw new IllegalArgumentException("Initial balance cannot be NaN");
        }
        if (initialBalance < Constants.MIN_ACCOUNT_INITIAL_BALANCE) {
            throw new IllegalArgumentException("Initial balance must be at least " + Constants.MIN_ACCOUNT_INITIAL_BALANCE);
        }
        if (initialBalance > Double.MAX_VALUE) {
            throw new IllegalArgumentException("Initial balance exceeds maximum value");
        }
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (Double.isNaN(amount)) {
            throw new IllegalArgumentException("Deposit amount cannot be NaN");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        double newBalance = this.balance + amount;
        if (newBalance < this.balance || newBalance > Double.MAX_VALUE || Double.isInfinite(newBalance)) {
            throw new IllegalArgumentException("Deposit would cause overflow");
        }
        this.balance = newBalance;
    }

    public void withdraw(double amount) {
        if (Double.isNaN(amount)) {
            throw new IllegalArgumentException("Withdrawal amount cannot be NaN");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        double newBalance = this.balance - amount;
        if (newBalance < 0 || newBalance > Double.MAX_VALUE || Double.isInfinite(newBalance)) {
            throw new IllegalArgumentException("Withdrawal would cause underflow");
        }
        this.balance = newBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountPin() {
        return accountPin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Account{accountNumber='%s', balance=%.2f}", accountNumber, balance);
    }
}
