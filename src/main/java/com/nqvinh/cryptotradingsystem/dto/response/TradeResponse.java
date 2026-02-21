package com.nqvinh.cryptotradingsystem.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeResponse(
    Long id,
    Long userId,
    String symbol,
    String side,
    BigDecimal quantity,
    BigDecimal price,
    BigDecimal quoteAmount,
    Instant createdAt) {}
