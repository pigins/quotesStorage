package com.birobot.quotes_storage.controller;

import com.birobot.quotes_storage.DateUtil;
import com.birobot.quotes_storage.database.QuotesDatabase;
import com.birobot.quotes_storage.dto.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

    private final QuotesDatabase db;

    @Autowired
    public Controller(QuotesDatabase db) {
        this.db = db;
    }

    @GetMapping("/symbols")
    public List<String> symbols() {
        return db.getAllQuoteTableNames();
    }

    // http://localhost:8080/klines?symbol=SNTBTC&startTime=1550865775000&endTime=1555865775000&limit=1000
    @GetMapping("/klines")
    public List<Candle> candles(
            @RequestParam("symbol") String symbol,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam("limit") Integer limit
            ) {
        return db.getKlines(symbol, DateUtil.toOffsetDateTime(startTime), DateUtil.toOffsetDateTime(endTime), limit);
    }
}
