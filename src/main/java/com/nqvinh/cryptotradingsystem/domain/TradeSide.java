package com.nqvinh.cryptotradingsystem.domain;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum TradeSide {
  BUY,
  SELL;

  /** String value for DB / API (same as enum name). */
  public String getCode() {
    return name();
  }

  public static TradeSide fromString(String s) {
    if (s == null || s.isBlank()) return null;
    String upper = s.trim().toUpperCase();
    for (TradeSide side : values()) {
      if (side.name().equals(upper)) return side;
    }
    return null;
  }

  public static String allowedValues() {
    return Arrays.stream(values()).map(TradeSide::getCode).collect(Collectors.joining(" or "));
  }
}
