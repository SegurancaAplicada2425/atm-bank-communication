package com.atmbank.bank.repository;

import com.atmbank.bank.model.Account;

public interface AccountRepository extends Repository<Account, String> {
    void updateBalance(String accountNumber, double newBalance);
}
