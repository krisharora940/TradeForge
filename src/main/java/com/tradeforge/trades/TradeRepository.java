package com.tradeforge.trades;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, UUID> {

    List<Trade> findByUserIdOrderByEntryTimeDesc(UUID userId);

    List<Trade> findByUserIdAndSymbolAndEntryTimeBetweenOrderByEntryTimeDesc(
            UUID userId,
            String symbol,
            Instant from,
            Instant to
    );

    Optional<Trade> findByIdAndUserId(UUID id, UUID userId);
}
