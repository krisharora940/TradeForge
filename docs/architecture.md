# Architecture

TradeForge is a modular monolith. Each domain package owns its entity and repository first, with services and controllers added in later milestones.

Current modules:

- `users`: user account persistence
- `auth`: registration, login, and token issuing
- `security`: JWT validation, password hashing, and route protection
- `marketdata`: OHLCV candle API, persistence, and Redis latest-candle cache
- `trades`: user-owned trade journal persistence
- `backtest`: async job persistence

Data ownership starts at the database layer. User-scoped tables reference `users(id)` and repositories include user-filtered lookup methods for protected queries added later.

Authentication is stateless. Login and registration return signed JWT bearer tokens. Spring Security validates tokens before protected controllers run.

Market data writes are normalized before persistence. Symbols are stored uppercase, timeframes lowercase, and duplicate candles are identified by symbol, timeframe, and candle time. Redis stores only the latest candle lookup cache; PostgreSQL remains the source of truth.
