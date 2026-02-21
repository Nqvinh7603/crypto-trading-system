package com.nqvinh.cryptotradingsystem.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("TRADE")
public record Trade(
    @Id Long id,
    @Column("USER_ID") Long userId,
    @Column("SYMBOL") String symbol,
    @Column("SIDE") String side,
    @Column("QUANTITY") BigDecimal quantity,
    @Column("PRICE") BigDecimal price,
    @Column("QUOTE_AMOUNT") BigDecimal quoteAmount,
    @Column("CREATED_AT") Instant createdAt) {}
