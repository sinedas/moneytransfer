package com.denisslav.moneytransfer.dto;

import java.math.BigDecimal;

public class AccountDto {
    private Long id;

    private BigDecimal balance;

    public AccountDto() {
    }

    public AccountDto(Long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
