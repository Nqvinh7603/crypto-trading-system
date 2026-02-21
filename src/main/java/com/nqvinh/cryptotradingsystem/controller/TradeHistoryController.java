package com.nqvinh.cryptotradingsystem.controller;

import com.nqvinh.cryptotradingsystem.dto.response.TradeHistoryResponse;
import com.nqvinh.cryptotradingsystem.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradeHistoryController {

  private final TradeService tradeService;

  @GetMapping(value = "/users/{userId}/trades", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<TradeHistoryResponse> getTradingHistory(
      @PathVariable Long userId, @RequestParam(defaultValue = "50") int limit) {
    return tradeService.getTradeHistory(userId, limit);
  }
}
