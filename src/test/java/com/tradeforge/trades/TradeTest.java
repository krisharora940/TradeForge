package com.tradeforge.trades;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.tradeforge.users.UserEntity;
import com.tradeforge.users.UserRole;

class TradeTest {

    @Test
    void onCreateSetsIdAndTimestamps() {
        UserEntity user = new UserEntity("test@example.com", "hash", UserRole.USER);
        Trade trade = new Trade(
                user,
                "MNQ",
                TradeSide.LONG,
                Instant.parse("2026-05-11T14:30:00Z"),
                BigDecimal.valueOf(18000),
                BigDecimal.valueOf(1)
        );

        trade.onCreate();

        assertThat(trade.getId()).isNotNull();
        assertThat(trade.getCreatedAt()).isNotNull();
        assertThat(trade.getUpdatedAt()).isNotNull();
    }
}
