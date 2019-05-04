package com.birobot.quotes_storage.controller;

import com.birobot.quotes_storage.DateUtil;
import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.database.QuotesDatabase;
import com.birobot.quotes_storage.dto.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class Controller {

    private final QuotesDatabase db;
    private final Client client;

    @Autowired
    public Controller(QuotesDatabase db, Client client) {
        this.db = db;
        this.client = client;
    }

    @GetMapping("/symbols/stored")
    public Set<String> storedSymbols() {
        return new HashSet<>(db.getAllQuoteTableNames());
    }

    @GetMapping("/symbols/available")
    public Set<String> availableSymbols() {
        return client.getAllSymbols();
    }

    // http://localhost:8080/klines/SNTBTC?startTime=1550865775000&endTime=1555865775000&limit=1000
    @GetMapping("/klines/{symbol}")
    public List<Candle> candles(
            @PathVariable String symbol,
            @RequestParam(required = false, name = "startTime") @Min(1262304000000L) Long startTime,
            @RequestParam(required = false, name = "endTime") @Min(1262304000000L) Long endTime,
            @RequestParam(required = false, name = "limit", defaultValue = "1000") @Min(1) Integer limit
    ) {
        return db.getKlines(
                symbol,
                startTime != null ? DateUtil.toOffsetDateTime(startTime) : null,
                endTime != null ? DateUtil.toOffsetDateTime(endTime) : null,
                limit
        );
    }
}