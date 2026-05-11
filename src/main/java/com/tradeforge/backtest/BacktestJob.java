package com.tradeforge.backtest;

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
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "backtest_jobs")
public class BacktestJob {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BacktestJobStatus status;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false, length = 16)
    private String timeframe;

    @Column(name = "strategy_name", nullable = false, length = 64)
    private String strategyName;

    @Column(name = "parameters_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String parametersJson;

    @Column(name = "result_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String resultJson;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected BacktestJob() {
    }

    public BacktestJob(UserEntity user, String symbol, String timeframe, String strategyName, String parametersJson) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.status = BacktestJobStatus.PENDING;
        this.symbol = symbol;
        this.timeframe = timeframe;
        this.strategyName = strategyName;
        this.parametersJson = parametersJson;
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = BacktestJobStatus.PENDING;
        }
        createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public BacktestJobStatus getStatus() {
        return status;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public String getParametersJson() {
        return parametersJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
