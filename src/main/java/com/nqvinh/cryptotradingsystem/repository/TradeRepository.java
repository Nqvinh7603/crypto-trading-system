package com.nqvinh.cryptotradingsystem.repository;

import com.nqvinh.cryptotradingsystem.domain.Trade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface TradeRepository extends R2dbcRepository<Trade, Long> {

  Flux<Trade> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  Flux<Trade> findByUserIdOrderByCreatedAtDesc(Long userId);
}
