# Crypto Trading System

Reactive Spring Boot application for crypto trading: aggregate best bid/ask from Binance and Huobi, execute trades at aggregated price, and manage user wallet balances. Supports **ETHUSDT** and **BTCUSDT** only.

---

## Tech Stack

| Layer        | Technology                    |
|-------------|-------------------------------|
| Runtime     | Java 21                        |
| Framework   | Spring Boot 4.0.3              |
| Web         | Spring WebFlux (reactive)      |
| Database    | H2 (in-memory) + Spring Data R2DBC |
| Build       | Maven 3.9.x                    |
| Other       | Lombok, MapStruct, Actuator   |

---

## Project Structure

```
src/main/java/com/nqvinh/cryptotradingsystem/
├── CryptoTradingSystemApplication.java   # Entry point
├── client/                                # External price feed clients
│   ├── PriceQuote.java                    # Common bid/ask model
│   ├── PriceFeedClient.java               # Interface (Strategy/DIP)
│   ├── PriceFeedSource.java               # Enum: BINANCE, HUOBI
│   ├── factory/
│   │   ├── PriceFeedClientFactory.java
│   │   └── impl/PriceFeedClientFactoryImpl.java
│   ├── binance/                           # BinanceClient + DTOs
│   └── huobi/                             # HuobiClient + DTOs
├── config/                                # App config & beans
│   ├── R2dbcConfig.java                   # Schema, data init, WebClient
│   ├── PriceFeedClientProperties.java     # app.price-feed.* from YAML
│   └── WalletSeeder.java                  # Seed 50,000 USDT for user 1
├── controller/                            # REST API (depends on service interfaces only)
│   ├── PriceController.java
│   ├── TradeController.java
│   ├── WalletController.java
│   └── TradeHistoryController.java
├── domain/                                # Entities & enums
│   ├── AggregatedPrice.java
│   ├── Wallet.java
│   ├── Trade.java
│   ├── SupportedSymbol.java               # ETHUSDT, BTCUSDT
│   └── TradeSide.java                     # BUY, SELL
├── dto/                                   # Request/response DTOs
│   ├── ApiError.java
│   ├── PriceDto.java
│   ├── request/TradeRequest.java
│   └── response/                         # LatestPriceResponse, TradeResponse, etc.
├── exception/
│   └── GlobalExceptionHandler.java       # 400 / 422 + ApiError
├── filter/
│   ├── ClientIpLoggingFilter.java         # Request log with clientIp, duration
│   └── ReactorContextClientIp.java       # Helper to get clientIp from context
├── mapper/                                # MapStruct: domain <-> DTO
│   ├── PriceMapper.java
│   ├── TradeMapper.java
│   └── WalletMapper.java
├── marketdata/                            # Price aggregation (scheduler)
│   ├── PriceAggregationService.java
│   └── PriceAggregationScheduler.java    # Every 10s
├── repository/                            # R2DBC repositories
│   ├── AggregatedPriceRepository.java
│   ├── WalletRepository.java
│   └── TradeRepository.java
└── service/                               # Interfaces + impl (no repo in controller)
    ├── PriceService.java / impl/
    ├── WalletService.java / impl/
    └── TradeService.java / impl/
```

---

## API Reference

Base path: **`/api`**. All responses are JSON. Assumption: user is already authenticated (no auth in this app).

### 1. Get latest aggregated price

**GET** `/api/prices/latest`

| Query   | Type   | Required | Description                    |
|---------|--------|----------|--------------------------------|
| `symbol`| string | No       | Filter by symbol (e.g. ETHUSDT). If omitted, returns both ETHUSDT and BTCUSDT. |

**Response 200**

```json
{
  "prices": [
    {
      "symbol": "ETHUSDT",
      "bestBid": "3500.12000000",
      "bestAsk": "3500.25000000",
      "createdAt": "2025-02-21T10:00:00Z"
    }
  ]
}
```

**Error 400** – Invalid or unsupported symbol  
Body: `{ "message": "No price for symbol: XYZ" }` or `Unsupported symbol...`

---

### 2. Execute trade (buy/sell)

**POST** `/api/trades`  
Content-Type: `application/json`

**Request body**

| Field     | Type    | Required | Description                          |
|-----------|---------|----------|--------------------------------------|
| `userId`  | number  | Yes      | User ID (e.g. 1)                     |
| `symbol`  | string  | Yes      | `ETHUSDT` or `BTCUSDT`               |
| `side`    | string  | Yes      | `BUY` or `SELL`                      |
| `quantity`| number  | Yes      | Amount of base asset (e.g. 1.5)      |

Example:

```json
{
  "userId": 1,
  "symbol": "ETHUSDT",
  "side": "BUY",
  "quantity": 1
}
```

**Response 200**

```json
{
  "id": 1,
  "userId": 1,
  "symbol": "ETHUSDT",
  "side": "BUY",
  "quantity": 1,
  "price": "3500.25",
  "quoteAmount": "3500.25",
  "createdAt": "2025-02-21T10:05:00Z"
}
```

**Error 400** – Bad request (e.g. missing userId, invalid symbol/side, insufficient balance)  
**Error 422** – No price available for symbol  
Body: `{ "message": "..." }`

---

### 3. Get wallet balance

**GET** `/api/users/{userId}/wallet`

**Response 200**

```json
{
  "balances": [
    { "asset": "USDT", "balance": 50000.0 },
    { "asset": "ETH", "balance": 0.5 }
  ]
}
```

---

### 4. Get trading history

**GET** `/api/users/{userId}/trades`

| Query   | Type | Default | Description        |
|---------|------|---------|--------------------|
| `limit` | int  | 50      | Max number of rows |

**Response 200**

```json
{
  "trades": [
    {
      "id": 1,
      "userId": 1,
      "symbol": "ETHUSDT",
      "side": "BUY",
      "quantity": 1,
      "price": "3500.25",
      "quoteAmount": "3500.25",
      "createdAt": "2025-02-21T10:05:00Z"
    }
  ]
}
```

---

## Configuration

`src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: crypto-trading-system
  r2dbc:
    url: r2dbc:h2:mem:///tradingdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
  h2:
    console:
      enabled: true

app:
  price-feed:
    binance:
      book-ticker-url: https://api.binance.com/api/v3/ticker/bookTicker
    huobi:
      tickers-url: https://api.huobi.pro/market/tickers
```

- **H2 console**: when enabled, available at `/h2-console` (JDBC URL format differs from R2DBC; use same DB name if needed).
- **Price feed URLs**: override for tests or different environments.

---

## Run & Test

**Build**

```bash
./mvnw clean compile
```

**Run**

```bash
./mvnw spring-boot:run
```

**Tests**

```bash
./mvnw test
```

**Packaging**

```bash
./mvnw package -DskipTests
java -jar target/crypto-trading-system-0.0.1-SNAPSHOT.jar
```

---

## Design Notes

- **Layers**: Controller → Service (interface) → Repository; controller does not use repository or mapper.
- **Client abstraction**: `PriceFeedClient` + `PriceFeedSource` enum; new exchange = new client impl + config.
- **Symbols / side**: `SupportedSymbol` and `TradeSide` enums instead of string literals.
- **Request logging**: `ClientIpLoggingFilter` logs method, path, client IP (X-Forwarded-For / X-Real-IP / remote), status, duration; client IP is also in Reactor context for business logs if needed.

---

## Seed Data

- User **1** is seeded with **50,000 USDT** (via `data.sql` and `WalletSeeder`). Use `userId: 1` for quick testing.
