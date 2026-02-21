package com.nqvinh.cryptotradingsystem.client.factory.impl;

import com.nqvinh.cryptotradingsystem.client.PriceFeedClient;
import com.nqvinh.cryptotradingsystem.client.factory.PriceFeedClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceFeedClientFactoryImpl implements PriceFeedClientFactory {

  private final List<PriceFeedClient> clients;

  @Override
  public List<PriceFeedClient> getClients() {
    return clients;
  }
}
