package com.denisslav.moneytransfer.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class Account {
    private Long id;

    private AtomicReference<BigDecimal> balance;

    public Account(Long id, AtomicReference<BigDecimal> balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public AtomicReference<BigDecimal> getBalance() {
        return balance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Account account = (Account) o;

        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        return balance != null ? balance.equals(account.balance) : account.balance == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}
