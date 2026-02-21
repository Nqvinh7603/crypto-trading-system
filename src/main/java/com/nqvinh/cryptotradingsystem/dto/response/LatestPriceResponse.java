package com.nqvinh.cryptotradingsystem.dto.response;

import com.nqvinh.cryptotradingsystem.dto.PriceDto;

import java.util.List;

public record LatestPriceResponse(List<PriceDto> prices) {}
