package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.config.ProxySocketAddress;
import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.dto.ExchangeInfo;
import okhttp3.OkHttpClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class CompositeClient implements Client {
    public CompositeClient(OkHttpClient okHttpClient, List<ProxySocketAddress> proxies) {

    }

    @Override
    public void init() {

    }

    @Override
    public List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate) {
        return null;
    }

    @Override
    public OffsetDateTime getDateOfFirstTrade(String symbol) {
        return null;
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        return null;
    }

    @Override
    public Set<String> getAllSymbols() {
        return null;
    }

    @Override
    public boolean isExhausted() {
        return false;
    }
}
