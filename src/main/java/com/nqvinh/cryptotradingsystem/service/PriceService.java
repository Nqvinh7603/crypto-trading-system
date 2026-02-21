package com.nqvinh.cryptotradingsystem.service;

import com.nqvinh.cryptotradingsystem.dto.response.LatestPriceResponse;
import reactor.core.publisher.Mono;

public interface PriceService {

  Mono<LatestPriceResponse> getLatest(String symbol);
}
