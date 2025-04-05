package com.atmbank.bank.repository;

import com.atmbank.bank.model.Account;

public interface AccountRepository extends Repository<Account, String> {
    boolean createAccount(String accountNumber, String accountPin, double initialBalance);

    boolean deposit(String accountNumber, String accountPin, double amount);

    boolean withdraw(String accountNumber, String accountPin, double amount);

    Account getAccount(String accountNumber, String accountPin);
}
