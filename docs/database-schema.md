# Database Schema

Milestone 2 creates the core schema through Flyway migration `V1__create_core_schema.sql`.

## Tables

### users

Stores platform users.

- `id` UUID primary key
- `email` unique, required
- `password_hash` required
- `role` required
- `created_at`, `updated_at`

### market_candles

Stores OHLCV market candles.

- `id` UUID primary key
- `symbol`, `timeframe`, `candle_time`
- `open`, `high`, `low`, `close`, `volume`
- `created_at`
- unique: `symbol`, `timeframe`, `candle_time`

### trades

Stores user-owned journaled trades.

- `id` UUID primary key
- `user_id` references `users(id)`
- `symbol`, `side`
- entry and exit fields
- `pnl`, `notes`
- `created_at`, `updated_at`

### backtest_jobs

Stores async backtest jobs.

- `id` UUID primary key
- `user_id` references `users(id)`
- `status`, `symbol`, `timeframe`, `strategy_name`
- `parameters_json`, `result_json`, `error_message`
- lifecycle timestamps

## Indexes

- candles by `symbol`, `timeframe`, `candle_time`
- trades by `user_id`, `entry_time`
- trades by `user_id`, `symbol`, `entry_time`
- backtest jobs by `user_id`, `created_at`
- backtest jobs by `user_id`, `status`
