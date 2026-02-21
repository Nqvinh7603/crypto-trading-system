package com.nqvinh.cryptotradingsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.price-feed")
public class PriceFeedClientProperties {

  private Binance binance = new Binance();
  private Huobi huobi = new Huobi();

  @Data
  public static class Binance {
    private String bookTickerUrl = "https://api.binance.com/api/v3/ticker/bookTicker";
  }

  @Data
  public static class Huobi {
    private String tickersUrl = "https://api.huobi.pro/market/tickers";
  }
}
