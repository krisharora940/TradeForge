package com.tradeforge.marketdata;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CandleRequest(
        @NotBlank String symbol,
        @NotBlank String timeframe,
        @NotNull Instant candleTime,
        @NotNull @Positive BigDecimal open,
        @NotNull @Positive BigDecimal high,
        @NotNull @Positive BigDecimal low,
        @NotNull @Positive BigDecimal close,
        @NotNull @PositiveOrZero BigDecimal volume
) {

    @AssertTrue(message = "high must be greater than or equal to low")
    public boolean isPriceRangeValid() {
        return high == null || low == null || high.compareTo(low) >= 0;
    }
}
