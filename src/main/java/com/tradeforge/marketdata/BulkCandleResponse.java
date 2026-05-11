package com.tradeforge.marketdata;

public record BulkCandleResponse(
        int requested,
        int inserted,
        int skipped
) {
}
