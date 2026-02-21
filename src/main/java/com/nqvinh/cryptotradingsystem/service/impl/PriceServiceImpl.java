package com.nqvinh.cryptotradingsystem.service.impl;

import com.nqvinh.cryptotradingsystem.domain.SupportedSymbol;
import com.nqvinh.cryptotradingsystem.dto.response.LatestPriceResponse;
import com.nqvinh.cryptotradingsystem.mapper.PriceMapper;
import com.nqvinh.cryptotradingsystem.repository.AggregatedPriceRepository;
import com.nqvinh.cryptotradingsystem.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

  private final AggregatedPriceRepository aggregatedPriceRepository;
  private final PriceMapper priceMapper;

  @Override
  public Mono<LatestPriceResponse> getLatest(String symbol) {
    if (symbol != null && !symbol.isBlank()) {
      String sym = symbol.trim().toUpperCase();
      if (SupportedSymbol.fromString(sym) == null) {
        String allowed = String.join(", ", SupportedSymbol.allSymbols());
        return Mono.error(
            new IllegalArgumentException("Unsupported symbol: " + sym + ". Use " + allowed));
      }
      return aggregatedPriceRepository
          .findFirstBySymbolOrderByCreatedAtDesc(sym)
          .map(priceMapper::toDto)
          .map(p -> new LatestPriceResponse(List.of(p)))
          .switchIfEmpty(
              Mono.error(() -> new IllegalArgumentException("No price for symbol: " + sym)));
    }
    return Flux.merge(
            Arrays.stream(SupportedSymbol.values())
                .map(
                    s ->
                        aggregatedPriceRepository.findFirstBySymbolOrderByCreatedAtDesc(
                            s.getSymbol()))
                .toList())
        .map(priceMapper::toDto)
        .collectList()
        .map(LatestPriceResponse::new);
  }
}
