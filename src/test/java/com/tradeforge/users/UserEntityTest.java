package com.tradeforge.users;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserEntityTest {

    @Test
    void onCreateSetsIdAndTimestamps() {
        UserEntity user = new UserEntity("test@example.com", "hash", UserRole.USER);

        user.onCreate();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }
}
