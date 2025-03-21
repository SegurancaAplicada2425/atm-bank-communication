package com.atmbank.bank.repository;

import com.atmbank.bank.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Account findById(String accountNumber) {
        return accounts.get(accountNumber);
    }

    @Override
    public Account save(Account account) {
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    @Override
    public void updateBalance(String accountNumber, double newBalance) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            account.setBalance(newBalance);
        }
    }
}
