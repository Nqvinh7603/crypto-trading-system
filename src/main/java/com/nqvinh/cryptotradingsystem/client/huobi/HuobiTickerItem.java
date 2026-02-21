package com.nqvinh.cryptotradingsystem.client.huobi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HuobiTickerItem(String symbol, Double bid, Double ask) {}
