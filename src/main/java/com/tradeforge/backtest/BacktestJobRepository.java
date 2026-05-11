package com.tradeforge.backtest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BacktestJobRepository extends JpaRepository<BacktestJob, UUID> {

    List<BacktestJob> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<BacktestJob> findByIdAndUserId(UUID id, UUID userId);
}
