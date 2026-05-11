package com.tradeforge.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @Test
    void healthReturnsUpStatus() {
        HealthController controller = new HealthController();

        Map<String, Object> response = controller.health();

        assertThat(response)
                .containsEntry("status", "UP")
                .containsEntry("service", "tradeforge-backend")
                .containsKey("timestamp");
    }
}
