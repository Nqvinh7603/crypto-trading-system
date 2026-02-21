package com.nqvinh.cryptotradingsystem.service.impl;

import com.nqvinh.cryptotradingsystem.dto.response.WalletBalanceResponse;
import com.nqvinh.cryptotradingsystem.mapper.WalletMapper;
import com.nqvinh.cryptotradingsystem.repository.WalletRepository;
import com.nqvinh.cryptotradingsystem.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final WalletMapper walletMapper;

  @Override
  public Mono<WalletBalanceResponse> getWalletBalance(Long userId) {
    return walletRepository
        .findByUserId(userId)
        .map(walletMapper::toBalanceItem)
        .collectList()
        .map(WalletBalanceResponse::new);
  }
}
