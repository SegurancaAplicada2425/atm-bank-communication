package com.atmbank.bank.repository;

import com.atmbank.bank.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, ReadWriteLock> accountLocks = new ConcurrentHashMap<>();

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
    public boolean createAccount(String accountNumber, String accountPin, double initialBalance) {
        if (accountNumber == null || accountPin == null || initialBalance < 0) {
            return false;
        }

        ReadWriteLock lock = getAccountLock(accountNumber);
        lock.writeLock().lock();
        try {
            Account existingAccount = findById(accountNumber);
            if (existingAccount != null) {
                return false;
            }

            Account newAccount = new Account(accountNumber, accountPin, initialBalance);
            save(newAccount);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean deposit(String accountNumber, String accountPin, double amount) {
        if (accountNumber == null || accountPin == null || amount <= 0) {
            return false;
        }

        ReadWriteLock lock = getAccountLock(accountNumber);
        lock.writeLock().lock();
        try {
            Account account = findById(accountNumber);
            if (account == null || !account.getAccountPin().equals(accountPin)) {
                return false;
            }

            account.deposit(amount);
            save(account);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean withdraw(String accountNumber, String accountPin, double amount) {
        if (accountNumber == null || accountPin == null || amount <= 0) {
            return false;
        }

        ReadWriteLock lock = getAccountLock(accountNumber);
        lock.writeLock().lock();
        try {
            Account account = findById(accountNumber);
            if (account == null || !account.getAccountPin().equals(accountPin)) {
                return false;
            }

            account.withdraw(amount);
            save(account);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Account getAccount(String accountNumber, String accountPin) {
        if (accountNumber == null || accountPin == null) {
            return null;
        }

        ReadWriteLock lock = getAccountLock(accountNumber);
        lock.readLock().lock();
        try {
            Account account = findById(accountNumber);
            if (account != null && account.getAccountPin().equals(accountPin)) {
                return account;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    private ReadWriteLock getAccountLock(String accountNumber) {
        return accountLocks.computeIfAbsent(accountNumber, k -> new ReentrantReadWriteLock());
    }
}
