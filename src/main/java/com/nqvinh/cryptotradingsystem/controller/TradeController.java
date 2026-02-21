package com.nqvinh.cryptotradingsystem.controller;

import com.nqvinh.cryptotradingsystem.dto.ApiError;
import com.nqvinh.cryptotradingsystem.dto.request.TradeRequest;
import com.nqvinh.cryptotradingsystem.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

  private final TradeService tradeService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Object>> createTrade(@RequestBody TradeRequest request) {
    if (request == null || request.userId() == null) {
      return Mono.just(
          ResponseEntity.badRequest().body((Object) new ApiError("userId is required")));
    }
    return tradeService
        .executeTrade(
            request.userId(),
            request.symbol(),
            request.side(),
            request.quantity() != null ? request.quantity() : BigDecimal.ZERO)
        .map(t -> ResponseEntity.ok((Object) t))
        .onErrorResume(
            IllegalArgumentException.class,
            e -> Mono.just(ResponseEntity.badRequest().body((Object) new ApiError(e.getMessage()))))
        .onErrorResume(
            IllegalStateException.class,
            e ->
                Mono.just(
                    ResponseEntity.unprocessableContent()
                        .body((Object) new ApiError(e.getMessage()))));
  }
}
