# TradeForge Backend

TradeForge Backend is a Spring Boot portfolio backend for market data, trade journaling, backtesting, and real-time job updates.

Current milestone: **Milestone 2 - Database + Flyway**.

## Tech Stack

- Java 21
- Spring Boot 3.5.13
- Maven
- PostgreSQL
- Redis
- Spring Security
- Flyway
- WebSocket
- Springdoc OpenAPI
- JUnit 5 and Testcontainers

## Local Setup

Prerequisites:

- Java 21
- Maven 3.9+
- Docker Desktop or compatible Docker runtime

Start PostgreSQL and Redis:

```bash
docker compose up -d
```

Run the app:

```bash
mvn spring-boot:run
```

Check health:

```bash
curl http://localhost:8080/health
curl http://localhost:8080/actuator/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Run tests:

```bash
mvn test
```

Validate Docker Compose:

```bash
docker compose config
```

Apply schema:

```bash
docker compose up -d
mvn spring-boot:run
```

Flyway runs automatically on startup and creates the core tables.

## Configuration

Default local values are set in `src/main/resources/application.yml`.

Override with environment variables:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
REDIS_HOST
REDIS_PORT
```

## Milestone 1 Scope

Included:

- Spring Boot application scaffold
- Clean package structure
- PostgreSQL and Redis Docker Compose services
- `/health` and `/actuator/health`
- Maven dependencies for the requested backend stack

Not included yet:

- Authentication
- Database schema migrations
- Market data APIs
- Trade journal APIs
- Backtesting
- WebSocket events

## Milestone 2 Scope

Included:

- Flyway migration for users, market candles, trades, and backtest jobs
- UUID primary keys and user-owned foreign keys
- Unique candle constraint on symbol, timeframe, and candle time
- Query indexes for candle lookup, trade history, and backtest polling
- JPA entities and repositories for the core domain tables

Still not included:

- Authentication endpoints
- Market data APIs
- Trade journal APIs
- Backtest execution
