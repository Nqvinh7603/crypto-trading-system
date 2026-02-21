package com.nqvinh.cryptotradingsystem.client.huobi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HuobiTickersResponse(List<HuobiTickerItem> data) {}
