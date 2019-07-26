package com.denisslav.moneytransfer.service;

import com.denisslav.moneytransfer.exception.AccountNotFoundException;
import com.denisslav.moneytransfer.exception.NotCorrectAmountException;
import com.denisslav.moneytransfer.exception.NotEnoughBalanceException;
import com.denisslav.moneytransfer.exception.SameAccountException;
import com.denisslav.moneytransfer.model.Account;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class TransferService {
    private AtomicLong counter = new AtomicLong(0);
    private Map<Long, Account> accountMap = new ConcurrentHashMap<>();

    public Account createAccount(BigDecimal initial) {
        Long id = counter.getAndIncrement();
        Account account = new Account(id, new AtomicReference<>(initial));
        accountMap.put(id, account);
        return account;
    }

    public Account getAccount(Long id) {
        Account account = accountMap.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }

        return account;
    }

    public List<Account> transfer(Long from, Long to, BigDecimal amount) {
        if (from.equals(to)) {
            throw new SameAccountException();
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) != 1) {
            throw new NotCorrectAmountException();
        }

        Account accountFrom = getAccount(from);
        Account accountTo = getAccount(to);

        synchronized (accountMap) {
            // It seems that sync by separate accounts
            //synchronized (from < to ? accountFrom : accountTo) {
            //    synchronized (from < to ? accountTo : accountFrom) {
            if (accountFrom.getBalance().get().compareTo(amount) < 0) {
                throw new NotEnoughBalanceException();
            }

            accountFrom.getBalance().set(accountFrom.getBalance().get().subtract(amount));
            accountTo.getBalance().set(accountTo.getBalance().get().add(amount));
            return List.of(accountFrom, accountTo);
        }//}
    }
}
