package com.nqvinh.cryptotradingsystem.controller;

import com.nqvinh.cryptotradingsystem.dto.response.LatestPriceResponse;
import com.nqvinh.cryptotradingsystem.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping(value = "/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<LatestPriceResponse> getLatest(@RequestParam(required = false) String symbol) {
        return priceService.getLatest(symbol);
    }
}
