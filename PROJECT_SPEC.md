# Project Spec: TradeForge Backend

## Objective

Build a production-style Java backend platform that proves at least 1 year of backend development capability.

This must not be a toy CRUD app. It must demonstrate:
- Java backend architecture
- REST API design
- authentication
- PostgreSQL persistence
- Redis caching
- async job processing
- WebSocket streaming
- integration tests
- Dockerized local development
- clean documentation

The final GitHub repo should look like a serious backend engineering portfolio project.

---

## Project Name

TradeForge Backend

A backend system for storing market data, journaling trades, running simple strategy backtests, and streaming job/account updates.

---

## Tech Stack

Use:

- Java 21
- Spring Boot 3.5.x
- Maven
- PostgreSQL
- Redis
- Spring Security
- JWT authentication
- Spring Data JPA
- Flyway
- Docker Compose
- JUnit 5
- Mockito
- Testcontainers
- OpenAPI / Swagger
- WebSockets with STOMP

Do not use a frontend unless needed for a minimal WebSocket demo page.

---

## Repository Structure

```text
tradeforge-backend/
├── README.md
├── PROJECT_SPEC.md
├── docker-compose.yml
├── pom.xml
├── docs/
│   ├── architecture.md
│   ├── api-examples.md
│   ├── database-schema.md
│   └── screenshots/
├── src/
│   ├── main/
│   │   ├── java/com/tradeforge/
│   │   │   ├── TradeForgeApplication.java
│   │   │   ├── config/
│   │   │   ├── security/
│   │   │   ├── auth/
│   │   │   ├── users/
│   │   │   ├── marketdata/
│   │   │   ├── trades/
│   │   │   ├── backtest/
│   │   │   ├── websocket/
│   │   │   ├── common/
│   │   │   └── exceptions/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/
│       └── java/com/tradeforge/
```

---

## Core Domain

The backend has four main modules:

1. Auth
2. Market Data
3. Trade Journal
4. Backtesting

---

# Milestone 1: Project Setup

## Requirements

Create a Spring Boot app with:

- Maven
- Java 21
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Security
- Validation
- Flyway
- Redis
- WebSocket
- Springdoc OpenAPI
- Testcontainers

## Deliverables

- App starts successfully
- `/actuator/health` or custom `/health` endpoint works
- Docker Compose starts PostgreSQL and Redis
- README has setup instructions

## Acceptance Criteria

Running:

```bash
docker compose up -d
mvn spring-boot:run
```

must start the app without errors.

---

# Milestone 2: Database + Flyway

## Requirements

Create Flyway migrations for:

### users

Fields:

- id UUID primary key
- email unique not null
- password_hash not null
- role
- created_at
- updated_at

### market_candles

Fields:

- id UUID primary key
- symbol
- timeframe
- candle_time
- open
- high
- low
- close
- volume
- created_at

Unique constraint:

- symbol + timeframe + candle_time

### trades

Fields:

- id UUID primary key
- user_id foreign key
- symbol
- side
- entry_time
- exit_time
- entry_price
- exit_price
- quantity
- pnl
- notes
- created_at
- updated_at

### backtest_jobs

Fields:

- id UUID primary key
- user_id foreign key
- status
- symbol
- timeframe
- strategy_name
- parameters_json
- result_json
- error_message
- created_at
- started_at
- completed_at

## Deliverables

- Flyway migration files
- JPA entities
- repositories
- database indexes

## Acceptance Criteria

App must create schema automatically through Flyway.

---

# Milestone 3: Authentication

## Requirements

Implement JWT auth.

Endpoints:

```http
POST /api/auth/register
POST /api/auth/login
GET /api/users/me
```

Register request:

```json
{
  "email": "test@example.com",
  "password": "Password123!"
}
```

Login response:

```json
{
  "accessToken": "...",
  "tokenType": "Bearer"
}
```

## Security Rules

Public:

- `/api/auth/**`
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/health`

Protected:

- everything else

## Deliverables

- password hashing with BCrypt
- JWT service
- auth filter
- security config
- user service

## Acceptance Criteria

Unauthenticated users cannot access protected APIs.

---

# Milestone 4: Market Data API

## Requirements

Implement candle storage and retrieval.

Endpoints:

```http
POST /api/market-data/candles
POST /api/market-data/candles/bulk
GET /api/market-data/candles?symbol=MNQ&timeframe=1m&from=...&to=...
GET /api/market-data/candles/latest?symbol=MNQ&timeframe=1m
```

Use validation:

- symbol required
- timeframe required
- OHLC must be positive
- high must be >= low
- candle_time required

## Caching

Use Redis for latest candle lookup.

Cache key:

```text
latest-candle:{symbol}:{timeframe}
```

## Deliverables

- DTOs
- service layer
- controller
- repository
- Redis caching

## Acceptance Criteria

Bulk candle insert should upsert or ignore duplicates safely.

---

# Milestone 5: Trade Journal API

## Requirements

Implement trade CRUD and analytics.

Endpoints:

```http
POST /api/trades
GET /api/trades
GET /api/trades/{id}
PUT /api/trades/{id}
DELETE /api/trades/{id}
GET /api/trades/stats
GET /api/trades/equity-curve
```

Stats response:

```json
{
  "totalTrades": 25,
  "netPnl": 1420.50,
  "winRate": 0.56,
  "averageWin": 320.25,
  "averageLoss": -180.40,
  "profitFactor": 1.72
}
```

Equity curve response:

```json
[
  {
    "date": "2026-05-01",
    "cumulativePnl": 250.00
  }
]
```

## Deliverables

- User-owned trades only
- analytics service
- pagination for trade list
- filtering by symbol/date

## Acceptance Criteria

A user cannot access another user's trades.

---

# Milestone 6: Backtesting Engine

## Requirements

Implement async backtest jobs.

Endpoint:

```http
POST /api/backtests
GET /api/backtests/{jobId}
GET /api/backtests
```

Backtest request:

```json
{
  "symbol": "MNQ",
  "timeframe": "1m",
  "strategyName": "SMA_CROSSOVER",
  "parameters": {
    "fastPeriod": 9,
    "slowPeriod": 21
  },
  "from": "2026-01-01T09:30:00",
  "to": "2026-01-31T16:00:00"
}
```

Job statuses:

```text
PENDING
RUNNING
COMPLETED
FAILED
```

Strategies:

Start with one:

```text
SMA_CROSSOVER
```

Result JSON:

```json
{
  "netPnl": 850.25,
  "totalTrades": 14,
  "winRate": 0.5,
  "maxDrawdown": -320.75,
  "profitFactor": 1.45
}
```

## Implementation Rules

- creating a backtest returns immediately
- actual processing runs async
- job status updates in database
- errors are stored in `error_message`
- user can poll job by ID

## Deliverables

- async service
- strategy interface
- SMA crossover implementation
- backtest result model

## Acceptance Criteria

Submitting a backtest must not block the HTTP request.

---

# Milestone 7: WebSocket Updates

## Requirements

Add WebSocket support for backtest job updates.

Endpoint:

```text
/ws
```

Topic:

```text
/topic/backtests/{userId}
```

When a job status changes, publish:

```json
{
  "jobId": "...",
  "status": "COMPLETED",
  "message": "Backtest completed successfully"
}
```

## Deliverables

- WebSocket config
- event publisher
- simple test client HTML page in `/docs/websocket-demo.html`

## Acceptance Criteria

When a backtest finishes, a connected client receives an update.

---

# Milestone 8: Testing

## Requirements

Add tests for:

### Unit tests

- auth service
- trade stats service
- SMA crossover strategy
- backtest service

### Integration tests

Use Testcontainers for:

- PostgreSQL
- Redis

Test:

- register/login flow
- protected endpoint access
- candle insert/retrieve
- trade ownership isolation
- backtest job creation

## Deliverables

- meaningful test suite
- test coverage badge optional
- tests must run with:

```bash
mvn test
```

## Acceptance Criteria

No reliance on local PostgreSQL for tests.

---

# Milestone 9: Documentation

## README Must Include

Sections:

1. Project overview
2. Why this project exists
3. Tech stack
4. Architecture diagram
5. Features
6. Local setup
7. API examples
8. Database schema
9. Testing
10. Engineering decisions
11. Future improvements

## Engineering Decisions Section

Explain:

- why PostgreSQL was used
- why Redis was used
- why async backtesting exists
- how JWT auth works
- how user ownership is enforced
- how Testcontainers improves reliability
- known limitations

## Deliverables

- polished README
- architecture diagram
- API screenshots or curl examples
- Swagger screenshot

---

# Milestone 10: Deployment

## Requirements

Prepare for deployment.

Options:

- Render
- Railway
- Fly.io
- AWS Elastic Beanstalk

Minimum required:

- Dockerfile
- production `application-prod.yml`
- environment variable config
- deployment notes

## Environment Variables

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
REDIS_HOST
REDIS_PORT
JWT_SECRET
JWT_EXPIRATION_MS
```

## Acceptance Criteria

Repo must explain exactly how to run locally and how to deploy.

---

# Code Quality Rules

Codex must follow these rules:

1. Use controller-service-repository structure.
2. Do not put business logic in controllers.
3. Use DTOs, not entities, for API requests/responses.
4. Use validation annotations.
5. Use global exception handling.
6. Use UUID primary keys.
7. Use constructor injection.
8. Avoid field injection.
9. Use meaningful package names.
10. Add comments only where logic is non-obvious.
11. Keep methods small.
12. Do not over-engineer microservices. Use a modular monolith.
13. Every protected query must filter by authenticated user.
14. Every new feature must include tests.
15. README must stay updated after each milestone.

---

# API Error Format

Use this standard error format:

```json
{
  "timestamp": "2026-05-11T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/trades"
}
```

---

# Definition of Done

The project is complete when:

- app runs locally through Docker Compose
- auth works
- users can store trades
- users can retrieve stats and equity curve
- market candles can be inserted and queried
- backtests run asynchronously
- WebSocket updates work
- tests pass
- Swagger docs work
- README looks professional
- repo has clear screenshots and diagrams

---

# Suggested Commit Plan

Use one commit per milestone:

```text
chore: initialize Spring Boot backend
feat: add database schema and Flyway migrations
feat: implement JWT authentication
feat: add market data candle APIs
feat: add trade journal and analytics APIs
feat: implement async backtesting engine
feat: add websocket job updates
test: add unit and integration test coverage
docs: add architecture and API documentation
chore: add Dockerfile and deployment config
```

---

# Codex Execution Instructions

Work milestone by milestone.

For each milestone:

1. Read `PROJECT_SPEC.md`.
2. Implement only the current milestone.
3. Run tests.
4. Fix compile errors.
5. Update README if needed.
6. Summarize what changed.
7. Do not skip acceptance criteria.
8. Do not invent unrelated features.
9. Keep the app runnable at all times.
