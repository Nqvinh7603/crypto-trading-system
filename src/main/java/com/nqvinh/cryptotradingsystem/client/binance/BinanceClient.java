package com.nqvinh.cryptotradingsystem.client.binance;

import com.nqvinh.cryptotradingsystem.client.PriceFeedClient;
import com.nqvinh.cryptotradingsystem.client.PriceFeedSource;
import com.nqvinh.cryptotradingsystem.client.PriceQuote;
import com.nqvinh.cryptotradingsystem.config.PriceFeedClientProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinanceClient implements PriceFeedClient {

  private final WebClient webClient;
  private final PriceFeedClientProperties properties;

  @Override
  public Flux<PriceQuote> fetch(Set<String> symbols) {
    String url = properties.getBinance().getBookTickerUrl();
    return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToFlux(BinanceBookTicker.class)
        .filter(t -> symbols.contains(t.symbol()))
        .map(this::toPriceQuote);
  }

  @Override
  public PriceFeedSource getSource() {
    return PriceFeedSource.BINANCE;
  }

  private PriceQuote toPriceQuote(BinanceBookTicker t) {
    return new PriceQuote(t.symbol(), parse(t.bidPrice()), parse(t.askPrice()));
  }

  private static BigDecimal parse(String s) {
    if (s == null || s.isBlank()) return null;
    try {
      return new BigDecimal(s.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
