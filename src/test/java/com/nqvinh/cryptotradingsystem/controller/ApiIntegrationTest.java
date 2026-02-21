package com.nqvinh.cryptotradingsystem.controller;

import com.nqvinh.cryptotradingsystem.domain.AggregatedPrice;
import com.nqvinh.cryptotradingsystem.marketdata.PriceAggregationScheduler;
import com.nqvinh.cryptotradingsystem.repository.AggregatedPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    PriceAggregationScheduler priceAggregationScheduler;

    @Autowired
    AggregatedPriceRepository aggregatedPriceRepository;

    @BeforeEach
    void seedPrice() {
        AggregatedPrice eth = AggregatedPrice.of("ETHUSDT", new BigDecimal("3000"), new BigDecimal("3010"));
        AggregatedPrice btc = AggregatedPrice.of("BTCUSDT", new BigDecimal("60000"), new BigDecimal("60100"));
        aggregatedPriceRepository.save(eth).then(aggregatedPriceRepository.save(btc)).block();
    }

    @Test
    @Order(1)
    void getWalletBalance_returnsUser1Balance() {
        webTestClient.get().uri("/api/users/1/wallet")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balances").isArray()
                .jsonPath("$.balances[?(@.asset == 'USDT')].balance").isEqualTo(50000.0);
    }

    @Test
    @Order(2)
    void getLatestPrice_withoutSymbol_returnsBothSymbols() {
        webTestClient.get().uri("/api/prices/latest")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.prices").isArray()
                .jsonPath("$.prices.length()").isEqualTo(2);
    }

    @Test
    @Order(3)
    void getLatestPrice_withSymbol_returnsOne() {
        webTestClient.get().uri("/api/prices/latest?symbol=ETHUSDT")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.prices.length()").isEqualTo(1)
                .jsonPath("$.prices[0].symbol").isEqualTo("ETHUSDT")
                .jsonPath("$.prices[0].bestBid").isEqualTo(3000)
                .jsonPath("$.prices[0].bestAsk").isEqualTo(3010);
    }

    @Test
    @Order(4)
    void postTrade_buyThenWalletAndHistoryUpdated() {
        String body = """
                {"userId": 1, "symbol": "ETHUSDT", "side": "BUY", "quantity": 1}
                """;
        webTestClient.post().uri("/api/trades")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.symbol").isEqualTo("ETHUSDT")
                .jsonPath("$.side").isEqualTo("BUY")
                .jsonPath("$.quantity").isEqualTo(1);

        webTestClient.get().uri("/api/users/1/wallet")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balances[?(@.asset == 'USDT')].balance").isEqualTo(46990.0)
                .jsonPath("$.balances[?(@.asset == 'ETH')].balance").isEqualTo(1.0);

        webTestClient.get().uri("/api/users/1/trades")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.trades.length()").isEqualTo(1)
                .jsonPath("$.trades[0].side").isEqualTo("BUY")
                .jsonPath("$.trades[0].symbol").isEqualTo("ETHUSDT");
    }

    @Test
    @Order(5)
    void getTradingHistory_returnsEmptyWhenNoTrades() {
        webTestClient.get().uri("/api/users/999/trades")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.trades").isEmpty();
    }
}
