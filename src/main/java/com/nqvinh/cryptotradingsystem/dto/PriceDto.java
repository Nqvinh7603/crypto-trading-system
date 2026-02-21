package com.nqvinh.cryptotradingsystem.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceDto(String symbol, BigDecimal bestBid, BigDecimal bestAsk, Instant createdAt) {}
