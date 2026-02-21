package com.nqvinh.cryptotradingsystem.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("AGGREGATED_PRICE")
public record AggregatedPrice(
    @Id Long id,
    @Column("SYMBOL") String symbol,
    @Column("BEST_BID") BigDecimal bestBid,
    @Column("BEST_ASK") BigDecimal bestAsk,
    @Column("CREATED_AT") Instant createdAt) {
  public static AggregatedPrice of(String symbol, BigDecimal bestBid, BigDecimal bestAsk) {
    return new AggregatedPrice(null, symbol, bestBid, bestAsk, Instant.now());
  }
}
