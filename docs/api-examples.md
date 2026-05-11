# API Examples

## Health

```bash
curl http://localhost:8080/health
```

## Auth

Register:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"test@example.com","password":"Password123!"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"test@example.com","password":"Password123!"}'
```

Current user:

```bash
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <accessToken>"
```

## Market Data

Create one candle:

```bash
curl -X POST http://localhost:8080/api/market-data/candles \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer <accessToken>" \
  -d '{"symbol":"MNQ","timeframe":"1m","candleTime":"2026-05-11T14:30:00Z","open":18000,"high":18025,"low":17990,"close":18010,"volume":120}'
```

Bulk insert candles:

```bash
curl -X POST http://localhost:8080/api/market-data/candles/bulk \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer <accessToken>" \
  -d '[{"symbol":"MNQ","timeframe":"1m","candleTime":"2026-05-11T14:30:00Z","open":18000,"high":18025,"low":17990,"close":18010,"volume":120},{"symbol":"MNQ","timeframe":"1m","candleTime":"2026-05-11T14:31:00Z","open":18010,"high":18030,"low":18000,"close":18020,"volume":98}]'
```

Query a range:

```bash
curl 'http://localhost:8080/api/market-data/candles?symbol=MNQ&timeframe=1m&from=2026-05-11T14:30:00Z&to=2026-05-11T15:00:00Z' \
  -H "Authorization: Bearer <accessToken>"
```

Latest candle:

```bash
curl 'http://localhost:8080/api/market-data/candles/latest?symbol=MNQ&timeframe=1m' \
  -H "Authorization: Bearer <accessToken>"
```
