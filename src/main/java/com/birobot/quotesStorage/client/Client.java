package com.birobot.quotesStorage.client;

import com.birobot.quotesStorage.client.dto.Candle;
import com.birobot.quotesStorage.client.dto.ExchangeInfo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface Client {
    void init();

    List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate);

    OffsetDateTime getDateOfFirstTrade(String symbol);

    ExchangeInfo getExchangeInfo();

    Set<String> getAllSymbols();

    boolean isExhausted();
}
