package com.tradeforge.marketdata;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @PostMapping("/candles")
    @ResponseStatus(HttpStatus.CREATED)
    public CandleResponse createCandle(@Valid @RequestBody CandleRequest request) {
        return marketDataService.createCandle(request);
    }

    @PostMapping("/candles/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public BulkCandleResponse createCandles(@Valid @RequestBody @NotEmpty List<@Valid CandleRequest> requests) {
        return marketDataService.createCandles(requests);
    }

    @GetMapping("/candles")
    public List<CandleResponse> findCandles(
            @RequestParam @NotBlank String symbol,
            @RequestParam @NotBlank String timeframe,
            @RequestParam Instant from,
            @RequestParam Instant to
    ) {
        return marketDataService.findCandles(symbol, timeframe, from, to);
    }

    @GetMapping("/candles/latest")
    public CandleResponse findLatest(
            @RequestParam @NotBlank String symbol,
            @RequestParam @NotBlank String timeframe
    ) {
        return marketDataService.findLatest(symbol, timeframe);
    }
}
