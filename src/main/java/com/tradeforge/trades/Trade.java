package com.tradeforge.trades;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.tradeforge.users.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TradeSide side;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "exit_time")
    private Instant exitTime;

    @Column(name = "entry_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal entryPrice;

    @Column(name = "exit_price", precision = 19, scale = 6)
    private BigDecimal exitPrice;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 6)
    private BigDecimal pnl;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Trade() {
    }

    public Trade(UserEntity user, String symbol, TradeSide side, Instant entryTime,
                 BigDecimal entryPrice, BigDecimal quantity) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.symbol = symbol;
        this.side = side;
        this.entryTime = entryTime;
        this.entryPrice = entryPrice;
        this.quantity = quantity;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getSymbol() {
        return symbol;
    }

    public TradeSide getSide() {
        return side;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public Instant getExitTime() {
        return exitTime;
    }

    public BigDecimal getEntryPrice() {
        return entryPrice;
    }

    public BigDecimal getExitPrice() {
        return exitPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPnl() {
        return pnl;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
