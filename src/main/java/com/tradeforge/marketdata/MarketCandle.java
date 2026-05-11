package com.tradeforge.marketdata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "market_candles",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_market_candles_symbol_timeframe_time",
                columnNames = {"symbol", "timeframe", "candle_time"}
        )
)
public class MarketCandle {

    @Id
    private UUID id;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false, length = 16)
    private String timeframe;

    @Column(name = "candle_time", nullable = false)
    private Instant candleTime;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal open;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal high;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal low;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal close;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal volume;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MarketCandle() {
    }

    public MarketCandle(String symbol, String timeframe, Instant candleTime, BigDecimal open,
                        BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume) {
        this.id = UUID.randomUUID();
        this.symbol = symbol;
        this.timeframe = timeframe;
        this.candleTime = candleTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public Instant getCandleTime() {
        return candleTime;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
