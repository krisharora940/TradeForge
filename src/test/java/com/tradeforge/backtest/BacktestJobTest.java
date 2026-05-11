package com.tradeforge.backtest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.tradeforge.users.UserEntity;
import com.tradeforge.users.UserRole;

class BacktestJobTest {

    @Test
    void newJobDefaultsToPending() {
        UserEntity user = new UserEntity("test@example.com", "hash", UserRole.USER);
        BacktestJob job = new BacktestJob(user, "MNQ", "1m", "SMA_CROSSOVER", "{}");

        job.onCreate();

        assertThat(job.getId()).isNotNull();
        assertThat(job.getStatus()).isEqualTo(BacktestJobStatus.PENDING);
        assertThat(job.getCreatedAt()).isNotNull();
    }
}
