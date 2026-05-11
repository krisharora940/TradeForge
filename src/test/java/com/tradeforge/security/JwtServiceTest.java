package com.tradeforge.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

    private static final String SECRET = "test-secret-for-jwt-signing-32-bytes-minimum";
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-11T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void generatedTokenContainsUsernameAndValidExpiration() {
        JwtService jwtService = new JwtService(SECRET, 3_600_000, CLOCK);
        UserDetails userDetails = User.withUsername("test@example.com")
                .password("hash")
                .authorities("ROLE_USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("test@example.com");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }
}
