package com.nqvinh.cryptotradingsystem.marketdata;

import com.nqvinh.cryptotradingsystem.client.PriceFeedClient;
import com.nqvinh.cryptotradingsystem.client.PriceQuote;
import com.nqvinh.cryptotradingsystem.client.factory.PriceFeedClientFactory;
import com.nqvinh.cryptotradingsystem.domain.AggregatedPrice;
import com.nqvinh.cryptotradingsystem.domain.SupportedSymbol;
import com.nqvinh.cryptotradingsystem.repository.AggregatedPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceAggregationService {

  private final PriceFeedClientFactory priceFeedClientFactory;
  private final AggregatedPriceRepository aggregatedPriceRepository;

  public Mono<Void> fetchAndStoreAggregatedPrices() {
    Set<String> symbols = SupportedSymbol.allSymbols();
    List<PriceFeedClient> clients = priceFeedClientFactory.getClients();
    Mono<List<PriceQuote>> allQuotes =
        Flux.fromIterable(clients)
            .flatMap(
                client ->
                    client
                        .fetch(symbols)
                        .onErrorResume(
                            e -> {
                              log.warn(
                                  "{} fetch failed: {}", client.getSource().name(), e.getMessage());
                              return Flux.empty();
                            }))
            .collectList();

    return allQuotes
        .flatMap(
            quotes ->
                Flux.fromIterable(symbols)
                    .flatMap(symbol -> aggregateAndSave(symbol, quotes))
                    .then())
        .doOnSuccess(v -> log.debug("Price aggregation completed"))
        .doOnError(e -> log.error("Price aggregation error", e));
  }

  private Mono<AggregatedPrice> aggregateAndSave(String symbol, List<PriceQuote> quotes) {
    BigDecimal bestBid = null;
    BigDecimal bestAsk = null;
    for (PriceQuote q : quotes) {
      if (!symbol.equals(q.symbol())) continue;
      if (q.bid() != null) bestBid = bestBid == null ? q.bid() : bestBid.max(q.bid());
      if (q.ask() != null) bestAsk = bestAsk == null ? q.ask() : bestAsk.min(q.ask());
    }
    if (bestBid == null || bestAsk == null) {
      log.warn("No prices for symbol {} (bid={}, ask={})", symbol, bestBid, bestAsk);
      return Mono.empty();
    }
    return aggregatedPriceRepository.save(AggregatedPrice.of(symbol, bestBid, bestAsk));
  }
}
