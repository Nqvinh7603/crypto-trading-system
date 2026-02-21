package com.nqvinh.cryptotradingsystem.service;

import com.nqvinh.cryptotradingsystem.dto.response.TradeHistoryResponse;
import com.nqvinh.cryptotradingsystem.dto.response.TradeResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface TradeService {

  Mono<TradeResponse> executeTrade(Long userId, String symbol, String side, BigDecimal quantity);

  Mono<TradeHistoryResponse> getTradeHistory(Long userId, int limit);
}
