package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.dto.ExchangeInfo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class CompositeClient implements Client {
    private final ProxyClient proxyClient;
    private final SimpleClient simpleClient;

    public CompositeClient(ProxyClient proxyClient, SimpleClient simpleClient) {
        this.proxyClient = proxyClient;
        this.simpleClient = simpleClient;
    }

    @Override
    public List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate) {
        if (proxyClient.isExhausted()) {
            return simpleClient.getOneMinuteBars(symbol, beginDate);
        } else {
            return proxyClient.getOneMinuteBars(symbol, beginDate);
        }
    }

    @Override
    public OffsetDateTime getDateOfFirstTrade(String symbol) {
        if (proxyClient.isExhausted()) {
            return simpleClient.getDateOfFirstTrade(symbol);
        } else {
            return proxyClient.getDateOfFirstTrade(symbol);
        }
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        if (proxyClient.isExhausted()) {
            return simpleClient.getExchangeInfo();
        } else {
            return proxyClient.getExchangeInfo();
        }
    }

    @Override
    public Set<String> getAllSymbols() {
        if (proxyClient.isExhausted()) {
            return simpleClient.getAllSymbols();
        } else {
            return proxyClient.getAllSymbols();
        }
    }

    @Override
    public boolean isExhausted() {
        return proxyClient.isExhausted() && simpleClient.isExhausted();
    }
}
