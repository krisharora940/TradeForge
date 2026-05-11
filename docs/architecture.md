# Architecture

TradeForge is a modular monolith. Each domain package owns its entity and repository first, with services and controllers added in later milestones.

Current modules:

- `users`: user account persistence
- `marketdata`: OHLCV candle persistence
- `trades`: user-owned trade journal persistence
- `backtest`: async job persistence

Data ownership starts at the database layer. User-scoped tables reference `users(id)` and repositories include user-filtered lookup methods for protected queries added later.
