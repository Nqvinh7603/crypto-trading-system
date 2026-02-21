package com.nqvinh.cryptotradingsystem.service;

import com.nqvinh.cryptotradingsystem.dto.response.WalletBalanceResponse;
import reactor.core.publisher.Mono;

public interface WalletService {

  Mono<WalletBalanceResponse> getWalletBalance(Long userId);
}
