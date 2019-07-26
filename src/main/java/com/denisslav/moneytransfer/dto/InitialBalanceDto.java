package com.denisslav.moneytransfer.dto;

import java.math.BigDecimal;

public class InitialBalanceDto {
    private BigDecimal balance = BigDecimal.ZERO;

    public InitialBalanceDto() {
    }

    public InitialBalanceDto(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
