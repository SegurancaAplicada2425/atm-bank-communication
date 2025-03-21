package com.atmbank.bank.repository;

public interface Repository<T, ID> {
    T findById(ID id);

    T save(T entity);
}
