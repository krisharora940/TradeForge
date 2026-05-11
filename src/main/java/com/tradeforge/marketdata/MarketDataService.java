package com.tradeforge.marketdata;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradeforge.exceptions.ConflictException;
import com.tradeforge.exceptions.NotFoundException;

@Service
public class MarketDataService {

    private final MarketCandleRepository marketCandleRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public MarketDataService(
            MarketCandleRepository marketCandleRepository,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.marketCandleRepository = marketCandleRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CandleResponse createCandle(CandleRequest request) {
        MarketCandle candle = toEntity(request);
        if (marketCandleRepository.existsBySymbolAndTimeframeAndCandleTime(
                candle.getSymbol(), candle.getTimeframe(), candle.getCandleTime())) {
            throw new ConflictException("Candle already exists for symbol, timeframe, and candle time");
        }

        try {
            CandleResponse response = CandleResponse.from(marketCandleRepository.save(candle));
            refreshLatestCache(candle.getSymbol(), candle.getTimeframe());
            return response;
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Candle already exists for symbol, timeframe, and candle time");
        }
    }

    @Transactional
    public BulkCandleResponse createCandles(List<CandleRequest> requests) {
        int inserted = 0;
        for (CandleRequest request : requests) {
            MarketCandle candle = toEntity(request);
            if (marketCandleRepository.existsBySymbolAndTimeframeAndCandleTime(
                    candle.getSymbol(), candle.getTimeframe(), candle.getCandleTime())) {
                continue;
            }

            try {
                marketCandleRepository.saveAndFlush(candle);
                refreshLatestCache(candle.getSymbol(), candle.getTimeframe());
                inserted++;
            } catch (DataIntegrityViolationException ex) {
                // Another request inserted the same candle first; bulk imports stay idempotent.
            }
        }
        return new BulkCandleResponse(requests.size(), inserted, requests.size() - inserted);
    }

    public List<CandleResponse> findCandles(String symbol, String timeframe, Instant from, Instant to) {
        return marketCandleRepository.findBySymbolAndTimeframeAndCandleTimeBetweenOrderByCandleTimeAsc(
                        normalizeSymbol(symbol),
                        normalizeTimeframe(timeframe),
                        from,
                        to
                )
                .stream()
                .map(CandleResponse::from)
                .toList();
    }

    public CandleResponse findLatest(String symbol, String timeframe) {
        String normalizedSymbol = normalizeSymbol(symbol);
        String normalizedTimeframe = normalizeTimeframe(timeframe);
        return readLatestCache(normalizedSymbol, normalizedTimeframe)
                .orElseGet(() -> {
                    CandleResponse response = marketCandleRepository
                            .findTopBySymbolAndTimeframeOrderByCandleTimeDesc(normalizedSymbol, normalizedTimeframe)
                            .map(CandleResponse::from)
                            .orElseThrow(() -> new NotFoundException("No candles found"));
                    writeLatestCache(response);
                    return response;
                });
    }

    private MarketCandle toEntity(CandleRequest request) {
        return new MarketCandle(
                normalizeSymbol(request.symbol()),
                normalizeTimeframe(request.timeframe()),
                request.candleTime(),
                request.open(),
                request.high(),
                request.low(),
                request.close(),
                request.volume()
        );
    }

    private void refreshLatestCache(String symbol, String timeframe) {
        marketCandleRepository.findTopBySymbolAndTimeframeOrderByCandleTimeDesc(symbol, timeframe)
                .map(CandleResponse::from)
                .ifPresent(this::writeLatestCache);
    }

    private java.util.Optional<CandleResponse> readLatestCache(String symbol, String timeframe) {
        String value = redisTemplate.opsForValue().get(cacheKey(symbol, timeframe));
        if (value == null) {
            return java.util.Optional.empty();
        }

        try {
            return java.util.Optional.of(objectMapper.readValue(value, CandleResponse.class));
        } catch (JsonProcessingException ex) {
            redisTemplate.delete(cacheKey(symbol, timeframe));
            return java.util.Optional.empty();
        }
    }

    private void writeLatestCache(CandleResponse response) {
        try {
            redisTemplate.opsForValue().set(
                    cacheKey(response.symbol(), response.timeframe()),
                    objectMapper.writeValueAsString(response)
            );
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to cache latest candle", ex);
        }
    }

    private String cacheKey(String symbol, String timeframe) {
        return "latest-candle:" + symbol + ":" + timeframe;
    }

    private String normalizeSymbol(String symbol) {
        return symbol.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeTimeframe(String timeframe) {
        return timeframe.trim().toLowerCase(Locale.ROOT);
    }
}
