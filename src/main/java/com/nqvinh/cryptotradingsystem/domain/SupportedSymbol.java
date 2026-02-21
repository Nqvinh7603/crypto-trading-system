package com.nqvinh.cryptotradingsystem.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum SupportedSymbol {
  ETHUSDT("ETHUSDT", "ETH"),
  BTCUSDT("BTCUSDT", "BTC");

  private final String symbol;
  private final String baseAsset;

  SupportedSymbol(String symbol, String baseAsset) {
    this.symbol = symbol;
    this.baseAsset = baseAsset;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getBaseAsset() {
    return baseAsset;
  }

  /** All symbol strings for aggregation/query. */
  public static Set<String> allSymbols() {
    return Arrays.stream(values()).map(SupportedSymbol::getSymbol).collect(Collectors.toSet());
  }

  public static SupportedSymbol fromString(String s) {
    if (s == null || s.isBlank()) return null;
    String upper = s.trim().toUpperCase();
    for (SupportedSymbol sym : values()) {
      if (sym.symbol.equals(upper)) return sym;
    }
    return null;
  }

  public static boolean isSupported(String s) {
    return fromString(s) != null;
  }
}
