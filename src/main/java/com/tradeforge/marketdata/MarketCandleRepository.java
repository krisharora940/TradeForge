package com.tradeforge.marketdata;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketCandleRepository extends JpaRepository<MarketCandle, UUID> {

    List<MarketCandle> findBySymbolAndTimeframeAndCandleTimeBetweenOrderByCandleTimeAsc(
            String symbol,
            String timeframe,
            Instant from,
            Instant to
    );

    Optional<MarketCandle> findTopBySymbolAndTimeframeOrderByCandleTimeDesc(String symbol, String timeframe);

    boolean existsBySymbolAndTimeframeAndCandleTime(String symbol, String timeframe, Instant candleTime);
}
