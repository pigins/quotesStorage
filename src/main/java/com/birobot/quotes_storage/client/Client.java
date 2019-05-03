package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.dto.ExchangeInfo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface Client {

    List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate);

    OffsetDateTime getDateOfFirstOpen(String symbol);

    OffsetDateTime getDateOfLastClose(String symbol);

    ExchangeInfo getExchangeInfo();

    Set<String> getAllSymbols();

    boolean isAvailable();
}
