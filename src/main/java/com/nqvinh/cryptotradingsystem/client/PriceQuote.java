package com.nqvinh.cryptotradingsystem.client;

import java.math.BigDecimal;

public record PriceQuote(String symbol, BigDecimal bid, BigDecimal ask) {}
