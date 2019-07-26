package com.denisslav.moneytransfer.exception;

public class AccountNotFoundException extends RuntimeException {
    private final Long id;

    public AccountNotFoundException(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
