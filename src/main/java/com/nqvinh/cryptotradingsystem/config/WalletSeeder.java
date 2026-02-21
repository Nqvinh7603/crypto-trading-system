package com.nqvinh.cryptotradingsystem.config;

import com.nqvinh.cryptotradingsystem.domain.Wallet;
import com.nqvinh.cryptotradingsystem.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/** Ensures user 1 has initial 50,000 USDT per requirement (in case data.sql did not run). */
@Component
@Slf4j
@RequiredArgsConstructor
public class WalletSeeder implements ApplicationRunner {

  private static final long DEFAULT_USER_ID = 1L;
  private static final BigDecimal INITIAL_USDT = new BigDecimal("50000");

  private final WalletRepository walletRepository;

  @Override
  public void run(ApplicationArguments args) {
    walletRepository
        .findByUserIdAndAsset(DEFAULT_USER_ID, "USDT")
        .switchIfEmpty(
            Mono.defer(
                () -> {
                  Wallet wallet =
                      new Wallet(
                          null, DEFAULT_USER_ID, "USDT", INITIAL_USDT, java.time.Instant.now());
                  return walletRepository
                      .save(wallet)
                      .doOnSuccess(
                          w ->
                              log.info(
                                  "Seeded wallet: user {} with {} USDT",
                                  DEFAULT_USER_ID,
                                  INITIAL_USDT));
                }))
        .subscribe();
  }
}
