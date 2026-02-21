package com.nqvinh.cryptotradingsystem.client.factory;

import com.nqvinh.cryptotradingsystem.client.PriceFeedClient;

import java.util.List;

public interface PriceFeedClientFactory {

  /** Returns all configured price feed clients to aggregate from. */
  List<PriceFeedClient> getClients();
}
