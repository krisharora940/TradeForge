package com.tradeforge.marketdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.tradeforge.exceptions.ConflictException;
import com.tradeforge.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class MarketDataServiceTest {

    @Mock
    private MarketCandleRepository marketCandleRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void createCandleNormalizesInputAndCachesLatest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(marketCandleRepository.existsBySymbolAndTimeframeAndCandleTime(
                "MNQ", "1m", Instant.parse("2026-05-11T14:30:00Z"))).thenReturn(false);
        when(marketCandleRepository.save(any(MarketCandle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(marketCandleRepository.findTopBySymbolAndTimeframeOrderByCandleTimeDesc("MNQ", "1m"))
                .thenReturn(Optional.of(candle("2026-05-11T14:30:00Z")));

        MarketDataService service = service();
        CandleResponse response = service.createCandle(request(" mnq ", " 1M ", "2026-05-11T14:30:00Z"));

        ArgumentCaptor<MarketCandle> candleCaptor = ArgumentCaptor.forClass(MarketCandle.class);
        verify(marketCandleRepository).save(candleCaptor.capture());
        assertThat(candleCaptor.getValue().getSymbol()).isEqualTo("MNQ");
        assertThat(candleCaptor.getValue().getTimeframe()).isEqualTo("1m");
        assertThat(response.symbol()).isEqualTo("MNQ");
        verify(valueOperations).set(org.mockito.ArgumentMatchers.eq("latest-candle:MNQ:1m"), any(String.class));
    }

    @Test
    void createCandleRejectsDuplicate() {
        when(marketCandleRepository.existsBySymbolAndTimeframeAndCandleTime(
                "MNQ", "1m", Instant.parse("2026-05-11T14:30:00Z"))).thenReturn(true);

        assertThatThrownBy(() -> service().createCandle(request("MNQ", "1m", "2026-05-11T14:30:00Z")))
                .isInstanceOf(ConflictException.class);
        verify(marketCandleRepository, never()).save(any());
    }

    @Test
    void createCandlesSkipsDuplicates() {
        Instant firstTime = Instant.parse("2026-05-11T14:30:00Z");
        Instant secondTime = Instant.parse("2026-05-11T14:31:00Z");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(marketCandleRepository.existsBySymbolAndTimeframeAndCandleTime("MNQ", "1m", firstTime)).thenReturn(false);
        when(marketCandleRepository.existsBySymbolAndTimeframeAndCandleTime("MNQ", "1m", secondTime)).thenReturn(true);
        when(marketCandleRepository.saveAndFlush(any(MarketCandle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(marketCandleRepository.findTopBySymbolAndTimeframeOrderByCandleTimeDesc("MNQ", "1m"))
                .thenReturn(Optional.of(candle("2026-05-11T14:30:00Z")));

        BulkCandleResponse response = service().createCandles(List.of(
                request("MNQ", "1m", "2026-05-11T14:30:00Z"),
                request("MNQ", "1m", "2026-05-11T14:31:00Z")
        ));

        assertThat(response).isEqualTo(new BulkCandleResponse(2, 1, 1));
    }

    @Test
    void findLatestUsesCacheWhenAvailable() throws Exception {
        CandleResponse cached = new CandleResponse(
                java.util.UUID.randomUUID(),
                "MNQ",
                "1m",
                Instant.parse("2026-05-11T14:30:00Z"),
                BigDecimal.valueOf(18000),
                BigDecimal.valueOf(18025),
                BigDecimal.valueOf(17990),
                BigDecimal.valueOf(18010),
                BigDecimal.valueOf(120)
        );
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("latest-candle:MNQ:1m")).thenReturn(objectMapper.writeValueAsString(cached));

        CandleResponse response = service().findLatest("mnq", "1M");

        assertThat(response).isEqualTo(cached);
        verify(marketCandleRepository, never()).findTopBySymbolAndTimeframeOrderByCandleTimeDesc(any(), any());
    }

    @Test
    void findLatestFallsBackToRepository() {
        MarketCandle candle = candle("2026-05-11T14:30:00Z");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(marketCandleRepository.findTopBySymbolAndTimeframeOrderByCandleTimeDesc("MNQ", "1m"))
                .thenReturn(Optional.of(candle));

        CandleResponse response = service().findLatest("MNQ", "1m");

        assertThat(response.id()).isEqualTo(candle.getId());
        verify(valueOperations).set(org.mockito.ArgumentMatchers.eq("latest-candle:MNQ:1m"), any(String.class));
    }

    @Test
    void findLatestThrowsWhenMissing() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(marketCandleRepository.findTopBySymbolAndTimeframeOrderByCandleTimeDesc("MNQ", "1m"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findLatest("MNQ", "1m"))
                .isInstanceOf(NotFoundException.class);
    }

    private MarketDataService service() {
        return new MarketDataService(marketCandleRepository, redisTemplate, objectMapper);
    }

    private MarketCandle candle(String candleTime) {
        MarketCandle candle = new MarketCandle(
                "MNQ",
                "1m",
                Instant.parse(candleTime),
                BigDecimal.valueOf(18000),
                BigDecimal.valueOf(18025),
                BigDecimal.valueOf(17990),
                BigDecimal.valueOf(18010),
                BigDecimal.valueOf(120)
        );
        candle.onCreate();
        return candle;
    }

    private CandleRequest request(String symbol, String timeframe, String candleTime) {
        return new CandleRequest(
                symbol,
                timeframe,
                Instant.parse(candleTime),
                BigDecimal.valueOf(18000),
                BigDecimal.valueOf(18025),
                BigDecimal.valueOf(17990),
                BigDecimal.valueOf(18010),
                BigDecimal.valueOf(120)
        );
    }
}
