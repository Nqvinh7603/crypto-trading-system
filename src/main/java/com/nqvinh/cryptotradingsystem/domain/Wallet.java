package com.nqvinh.cryptotradingsystem.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("WALLET")
public record Wallet(
    @Id Long id,
    @Column("USER_ID") Long userId,
    @Column("ASSET") String asset,
    @Column("BALANCE") BigDecimal balance,
    @Column("UPDATED_AT") Instant updatedAt) {
  public Wallet withBalance(BigDecimal newBalance) {
    return new Wallet(id, userId, asset, newBalance, Instant.now());
  }
}
