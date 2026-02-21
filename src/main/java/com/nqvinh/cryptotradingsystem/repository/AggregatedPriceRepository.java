package com.nqvinh.cryptotradingsystem.repository;

import com.nqvinh.cryptotradingsystem.domain.AggregatedPrice;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AggregatedPriceRepository extends R2dbcRepository<AggregatedPrice, Long> {

  Mono<AggregatedPrice> findFirstBySymbolOrderByCreatedAtDesc(String symbol);
}
