package com.tradeforge.marketdata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CandleResponse(
        UUID id,
        String symbol,
        String timeframe,
        Instant candleTime,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        BigDecimal volume
) {

    public static CandleResponse from(MarketCandle candle) {
        return new CandleResponse(
                candle.getId(),
                candle.getSymbol(),
                candle.getTimeframe(),
                candle.getCandleTime(),
                candle.getOpen(),
                candle.getHigh(),
                candle.getLow(),
                candle.getClose(),
                candle.getVolume()
        );
    }
}
