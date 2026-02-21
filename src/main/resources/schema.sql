-- Aggregated best bid/ask per symbol (latest snapshot per 10s)
CREATE TABLE IF NOT EXISTS aggregated_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    best_bid DECIMAL(30, 8) NOT NULL,
    best_ask DECIMAL(30, 8) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_aggregated_price_symbol_created ON aggregated_price(symbol, created_at DESC);

-- User wallet balances per asset (USDT, ETH, BTC)
CREATE TABLE IF NOT EXISTS wallet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    asset VARCHAR(10) NOT NULL,
    balance DECIMAL(30, 8) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_wallet_user_asset UNIQUE (user_id, asset)
);

CREATE INDEX IF NOT EXISTS idx_wallet_user_id ON wallet(user_id);

-- Trading history
CREATE TABLE IF NOT EXISTS trade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    side VARCHAR(4) NOT NULL,
    quantity DECIMAL(30, 8) NOT NULL,
    price DECIMAL(30, 8) NOT NULL,
    quote_amount DECIMAL(30, 8) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_trade_user_id ON trade(user_id);
