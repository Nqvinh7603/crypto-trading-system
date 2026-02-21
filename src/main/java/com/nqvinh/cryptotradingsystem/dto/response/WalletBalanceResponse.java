package com.nqvinh.cryptotradingsystem.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record WalletBalanceResponse(List<BalanceItem> balances) {
  public record BalanceItem(String asset, BigDecimal balance) {}
}
