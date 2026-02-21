package com.nqvinh.cryptotradingsystem.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record TradeHistoryResponse(List<TradeItem> trades) {
  public record TradeItem(
      Long id,
      Long userId,
      String symbol,
      String side,
      BigDecimal quantity,
      BigDecimal price,
      BigDecimal quoteAmount,
      Instant createdAt) {}
}
