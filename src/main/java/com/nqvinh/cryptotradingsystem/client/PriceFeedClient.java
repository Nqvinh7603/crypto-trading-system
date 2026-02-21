package com.nqvinh.cryptotradingsystem.client;

import reactor.core.publisher.Flux;

import java.util.Set;

public interface PriceFeedClient {

  Flux<PriceQuote> fetch(Set<String> symbols);

  PriceFeedSource getSource();
}
