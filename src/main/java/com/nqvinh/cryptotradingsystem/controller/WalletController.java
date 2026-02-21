package com.nqvinh.cryptotradingsystem.controller;

import com.nqvinh.cryptotradingsystem.dto.response.WalletBalanceResponse;
import com.nqvinh.cryptotradingsystem.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WalletController {

  private final WalletService walletService;

  @GetMapping(value = "/users/{userId}/wallet", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<WalletBalanceResponse> getWalletBalance(@PathVariable Long userId) {
    return walletService.getWalletBalance(userId);
  }
}
