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
        if (proxyClient.isAvailable()) {
            return simpleClient.getOneMinuteBars(symbol, beginDate);
        } else {
            return proxyClient.getOneMinuteBars(symbol, beginDate);
        }
    }

    @Override
    public OffsetDateTime getDateOfFirstOpen(String symbol) {
        if (proxyClient.isAvailable()) {
            return simpleClient.getDateOfFirstOpen(symbol);
        } else {
            return proxyClient.getDateOfFirstOpen(symbol);
        }
    }

    @Override
    public OffsetDateTime getDateOfLastClose(String symbol) {
        if (proxyClient.isAvailable()) {
            return simpleClient.getDateOfLastClose(symbol);
        } else {
            return proxyClient.getDateOfLastClose(symbol);
        }
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        if (proxyClient.isAvailable()) {
            return simpleClient.getExchangeInfo();
        } else {
            return proxyClient.getExchangeInfo();
        }
    }

    @Override
    public Set<String> getAllSymbols() {
        if (proxyClient.isAvailable()) {
            return simpleClient.getAllSymbols();
        } else {
            return proxyClient.getAllSymbols();
        }
    }

    @Override
    public boolean isAvailable() {
        return proxyClient.isAvailable() && simpleClient.isAvailable();
    }
}
