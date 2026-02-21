package com.nqvinh.cryptotradingsystem.marketdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PriceAggregationScheduler {

  private final PriceAggregationService priceAggregationService;

  @Scheduled(fixedRate = 10_000) // 10 seconds
  public void runPriceAggregation() {
    priceAggregationService.fetchAndStoreAggregatedPrices().subscribe();
  }
}
