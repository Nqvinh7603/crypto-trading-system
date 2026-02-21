package com.nqvinh.cryptotradingsystem.dto.request;

import java.math.BigDecimal;

public record TradeRequest(Long userId, String symbol, String side, BigDecimal quantity) {}
