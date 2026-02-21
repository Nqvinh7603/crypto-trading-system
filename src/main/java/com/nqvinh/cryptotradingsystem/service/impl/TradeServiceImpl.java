package com.nqvinh.cryptotradingsystem.service.impl;

import com.nqvinh.cryptotradingsystem.domain.AggregatedPrice;
import com.nqvinh.cryptotradingsystem.domain.SupportedSymbol;
import com.nqvinh.cryptotradingsystem.domain.Trade;
import com.nqvinh.cryptotradingsystem.domain.TradeSide;
import com.nqvinh.cryptotradingsystem.domain.Wallet;
import com.nqvinh.cryptotradingsystem.dto.response.TradeHistoryResponse;
import com.nqvinh.cryptotradingsystem.dto.response.TradeResponse;
import com.nqvinh.cryptotradingsystem.mapper.TradeMapper;
import com.nqvinh.cryptotradingsystem.repository.AggregatedPriceRepository;
import com.nqvinh.cryptotradingsystem.repository.TradeRepository;
import com.nqvinh.cryptotradingsystem.repository.WalletRepository;
import com.nqvinh.cryptotradingsystem.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

  private static final int QUOTE_SCALE = 8;

  private final AggregatedPriceRepository aggregatedPriceRepository;
  private final WalletRepository walletRepository;
  private final TradeRepository tradeRepository;
  private final TradeMapper tradeMapper;

  @Override
  @Transactional
  public Mono<TradeResponse> executeTrade(
      Long userId, String symbol, String side, BigDecimal quantity) {
    String sym = symbol != null ? symbol.trim().toUpperCase() : "";
    SupportedSymbol supportedSymbol = SupportedSymbol.fromString(sym);
    if (supportedSymbol == null) {
      return Mono.error(
          new IllegalArgumentException(
              "Unsupported symbol: " + symbol + ". Use " + SupportedSymbol.allSymbols()));
    }
    TradeSide tradeSide = TradeSide.fromString(side);
    if (tradeSide == null) {
      return Mono.error(
          new IllegalArgumentException("Side must be " + TradeSide.allowedValues() + "."));
    }
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      return Mono.error(new IllegalArgumentException("Quantity must be positive."));
    }

    String baseAsset = supportedSymbol.getBaseAsset();
    return aggregatedPriceRepository
        .findFirstBySymbolOrderByCreatedAtDesc(sym)
        .switchIfEmpty(
            Mono.error(
                new IllegalStateException("No price available for " + sym + ". Try again later.")))
        .flatMap(price -> executeAtPrice(userId, sym, tradeSide, quantity, baseAsset, price))
        .map(tradeMapper::toResponse);
  }

  private Mono<Trade> executeAtPrice(
      Long userId,
      String symbol,
      TradeSide side,
      BigDecimal quantity,
      String baseAsset,
      AggregatedPrice price) {
    BigDecimal priceUsed = side == TradeSide.BUY ? price.bestAsk() : price.bestBid();
    BigDecimal quoteAmount =
        quantity.multiply(priceUsed).setScale(QUOTE_SCALE, RoundingMode.HALF_UP);

    if (side == TradeSide.BUY) {
      return buy(userId, symbol, quantity, baseAsset, priceUsed, quoteAmount);
    } else {
      return sell(userId, symbol, quantity, baseAsset, priceUsed, quoteAmount);
    }
  }

  private Mono<Trade> buy(
      Long userId,
      String symbol,
      BigDecimal quantity,
      String baseAsset,
      BigDecimal priceUsed,
      BigDecimal quoteAmount) {
    Mono<Wallet> usdt =
        walletRepository
            .findByUserIdAndAsset(userId, "USDT")
            .switchIfEmpty(Mono.error(new IllegalStateException("User wallet not found.")));
    Mono<Wallet> base =
        walletRepository
            .findByUserIdAndAsset(userId, baseAsset)
            .switchIfEmpty(
                Mono.just(new Wallet(null, userId, baseAsset, BigDecimal.ZERO, Instant.now())));

    return Mono.zip(usdt, base)
        .flatMap(
            tuple -> {
              Wallet usdtWallet = tuple.getT1();
              Wallet baseWallet = tuple.getT2();
              if (usdtWallet.balance().compareTo(quoteAmount) < 0) {
                return Mono.error(
                    new IllegalArgumentException(
                        "Insufficient USDT balance. Required: "
                            + quoteAmount
                            + ", available: "
                            + usdtWallet.balance()));
              }
              Wallet newUsdt = usdtWallet.withBalance(usdtWallet.balance().subtract(quoteAmount));
              Wallet newBase = baseWallet.withBalance(baseWallet.balance().add(quantity));
              Trade trade =
                  new Trade(
                      null,
                      userId,
                      symbol,
                      TradeSide.BUY.getCode(),
                      quantity,
                      priceUsed,
                      quoteAmount,
                      Instant.now());
              return walletRepository
                  .save(newUsdt)
                  .then(walletRepository.save(newBase))
                  .then(tradeRepository.save(trade));
            });
  }

  private Mono<Trade> sell(
      Long userId,
      String symbol,
      BigDecimal quantity,
      String baseAsset,
      BigDecimal priceUsed,
      BigDecimal quoteAmount) {
    return walletRepository
        .findByUserIdAndAsset(userId, baseAsset)
        .switchIfEmpty(
            Mono.error(new IllegalArgumentException("Insufficient " + baseAsset + " balance.")))
        .flatMap(
            baseWallet -> {
              if (baseWallet.balance().compareTo(quantity) < 0) {
                return Mono.error(
                    new IllegalArgumentException(
                        "Insufficient "
                            + baseAsset
                            + " balance. Required: "
                            + quantity
                            + ", available: "
                            + baseWallet.balance()));
              }
              return walletRepository
                  .findByUserIdAndAsset(userId, "USDT")
                  .switchIfEmpty(
                      Mono.just(new Wallet(null, userId, "USDT", BigDecimal.ZERO, Instant.now())))
                  .flatMap(
                      usdtWallet -> {
                        Wallet newBase =
                            baseWallet.withBalance(baseWallet.balance().subtract(quantity));
                        Wallet newUsdt =
                            usdtWallet.withBalance(usdtWallet.balance().add(quoteAmount));
                        Trade trade =
                            new Trade(
                                null,
                                userId,
                                symbol,
                                TradeSide.SELL.getCode(),
                                quantity,
                                priceUsed,
                                quoteAmount,
                                Instant.now());
                        return walletRepository
                            .save(newBase)
                            .then(walletRepository.save(newUsdt))
                            .then(tradeRepository.save(trade));
                      });
            });
  }

  @Override
  public Mono<TradeHistoryResponse> getTradeHistory(Long userId, int limit) {
    return tradeRepository
        .findByUserIdOrderByCreatedAtDesc(userId)
        .take(limit)
        .map(tradeMapper::toTradeItem)
        .collectList()
        .map(TradeHistoryResponse::new);
  }
}
