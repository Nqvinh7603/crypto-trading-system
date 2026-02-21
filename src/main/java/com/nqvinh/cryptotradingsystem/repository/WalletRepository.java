package com.nqvinh.cryptotradingsystem.repository;

import com.nqvinh.cryptotradingsystem.domain.Wallet;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletRepository extends R2dbcRepository<Wallet, Long> {

  Flux<Wallet> findByUserId(Long userId);

  Mono<Wallet> findByUserIdAndAsset(Long userId, String asset);
}
