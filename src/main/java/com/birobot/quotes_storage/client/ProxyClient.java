package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.config.ProxySocketAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.dto.ExchangeInfo;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProxyClient implements Client {
    private List<SimpleClient> clients;

    public ProxyClient(OkHttpClient okHttpClient, List<ProxySocketAddress> proxySocketAddresses, ObjectMapper objectMapper) {
        clients = proxySocketAddresses
                .stream()
                .map(proxySocketAddress -> new InetSocketAddress(proxySocketAddress.getHost(), proxySocketAddress.getPort()))
                .map(address -> new Proxy(Proxy.Type.HTTP, address))
                .map(proxy -> okHttpClient.newBuilder().proxy(proxy).build())
                .map(i -> new SimpleClient(okHttpClient, objectMapper)).collect(Collectors.toList());
    }

    @Override
    public List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate) {
        return getRecursive(anyClient -> anyClient.getOneMinuteBars(symbol, beginDate));
    }

    @Override
    public OffsetDateTime getDateOfFirstOpen(String symbol) {
        return getRecursive(anyClient -> anyClient.getDateOfFirstOpen(symbol));
    }

    @Override
    public OffsetDateTime getDateOfLastClose(String symbol) {
        return getRecursive(anyClient -> anyClient.getDateOfLastClose(symbol));
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        return getRecursive(SimpleClient::getExchangeInfo);
    }

    @Override
    public Set<String> getAllSymbols() {
        return getRecursive(SimpleClient::getAllSymbols);
    }

    @Override
    public boolean isAvailable() {
        return clients.stream().anyMatch(SimpleClient::isAvailable);
    }

    private SimpleClient getAnySimpleClient() {
        return clients.stream().filter(SimpleClient::isAvailable).findFirst().orElse(null);
    }

    private <T> T getRecursive(Function<SimpleClient, T> function) {
        SimpleClient anySimpleClient = getAnySimpleClient();
        if (anySimpleClient == null) {
            throw new RuntimeException("all proxies are not available");
        } else {
            try {
                return function.apply(anySimpleClient);
            } catch (ClientException e) {
                return getRecursive(function);
            }
        }
    }
}