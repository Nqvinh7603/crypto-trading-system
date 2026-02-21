package com.nqvinh.cryptotradingsystem.client.huobi;

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
public class HuobiClient implements PriceFeedClient {

  private final WebClient webClient;
  private final PriceFeedClientProperties properties;

  @Override
  public Flux<PriceQuote> fetch(Set<String> symbols) {
    String url = properties.getHuobi().getTickersUrl();
    return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(HuobiTickersResponse.class)
        .flatMapMany(r -> r.data() != null ? Flux.fromIterable(r.data()) : Flux.empty())
        .filter(t -> t.symbol() != null && symbols.contains(t.symbol().toUpperCase()))
        .map(this::toPriceQuote);
  }

  @Override
  public PriceFeedSource getSource() {
    return PriceFeedSource.HUOBI;
  }

  private PriceQuote toPriceQuote(HuobiTickerItem t) {
    BigDecimal bid = t.bid() != null ? BigDecimal.valueOf(t.bid()) : null;
    BigDecimal ask = t.ask() != null ? BigDecimal.valueOf(t.ask()) : null;
    return new PriceQuote(t.symbol().toUpperCase(), bid, ask);
  }
}
