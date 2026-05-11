package com.tradeforge.marketdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class MarketCandleTest {

    @Test
    void onCreateSetsIdAndTimestamp() {
        MarketCandle candle = new MarketCandle(
                "MNQ",
                "1m",
                Instant.parse("2026-05-11T14:30:00Z"),
                BigDecimal.valueOf(18000),
                BigDecimal.valueOf(18025),
                BigDecimal.valueOf(17990),
                BigDecimal.valueOf(18010),
                BigDecimal.valueOf(120)
        );

        candle.onCreate();

        assertThat(candle.getId()).isNotNull();
        assertThat(candle.getCreatedAt()).isNotNull();
    }
}
