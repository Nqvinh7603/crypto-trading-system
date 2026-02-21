package com.nqvinh.cryptotradingsystem.client.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BinanceBookTicker(String symbol, String bidPrice, String askPrice) {}
