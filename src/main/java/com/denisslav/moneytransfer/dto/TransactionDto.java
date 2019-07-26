package com.denisslav.moneytransfer.dto;

import java.math.BigDecimal;

public class TransactionDto {
    private Long from;
    private Long to;
    private BigDecimal amount;

    public TransactionDto() {
    }

    public TransactionDto(Long from, Long to, BigDecimal amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Long getFrom() {
        return from;
    }

    public Long getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
