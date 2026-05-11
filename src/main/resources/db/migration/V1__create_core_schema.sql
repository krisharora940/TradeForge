CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE market_candles (
    id UUID PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL,
    timeframe VARCHAR(16) NOT NULL,
    candle_time TIMESTAMPTZ NOT NULL,
    open NUMERIC(19, 6) NOT NULL,
    high NUMERIC(19, 6) NOT NULL,
    low NUMERIC(19, 6) NOT NULL,
    close NUMERIC(19, 6) NOT NULL,
    volume NUMERIC(19, 6) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_market_candles_symbol_timeframe_time UNIQUE (symbol, timeframe, candle_time),
    CONSTRAINT chk_market_candles_prices_positive CHECK (open > 0 AND high > 0 AND low > 0 AND close > 0),
    CONSTRAINT chk_market_candles_high_low CHECK (high >= low),
    CONSTRAINT chk_market_candles_volume_non_negative CHECK (volume >= 0)
);

CREATE TABLE trades (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    side VARCHAR(16) NOT NULL,
    entry_time TIMESTAMPTZ NOT NULL,
    exit_time TIMESTAMPTZ,
    entry_price NUMERIC(19, 6) NOT NULL,
    exit_price NUMERIC(19, 6),
    quantity NUMERIC(19, 6) NOT NULL,
    pnl NUMERIC(19, 6),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_trades_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_trades_entry_price_positive CHECK (entry_price > 0),
    CONSTRAINT chk_trades_exit_price_positive CHECK (exit_price IS NULL OR exit_price > 0),
    CONSTRAINT chk_trades_quantity_positive CHECK (quantity > 0)
);

CREATE TABLE backtest_jobs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    timeframe VARCHAR(16) NOT NULL,
    strategy_name VARCHAR(64) NOT NULL,
    parameters_json JSONB NOT NULL,
    result_json JSONB,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    CONSTRAINT fk_backtest_jobs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_market_candles_symbol_timeframe_time
    ON market_candles (symbol, timeframe, candle_time DESC);

CREATE INDEX idx_trades_user_entry_time
    ON trades (user_id, entry_time DESC);

CREATE INDEX idx_trades_user_symbol_entry_time
    ON trades (user_id, symbol, entry_time DESC);

CREATE INDEX idx_backtest_jobs_user_created_at
    ON backtest_jobs (user_id, created_at DESC);

CREATE INDEX idx_backtest_jobs_user_status
    ON backtest_jobs (user_id, status);
