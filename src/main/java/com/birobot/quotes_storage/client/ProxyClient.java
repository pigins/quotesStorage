package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.config.ProxySocketAddress;
import okhttp3.OkHttpClient;
import com.birobot.quotes_storage.client.dto.Candle;
import com.birobot.quotes_storage.client.dto.ExchangeInfo;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProxyClient implements Client {
    private final OkHttpClient okHttpClient;
    private final List<ProxySocketAddress> proxySocketAddresses;
    private List<SimpleClient> clients;

    public ProxyClient(OkHttpClient okHttpClient, List<ProxySocketAddress> proxySocketAddresses) {
        this.okHttpClient = okHttpClient;
        this.proxySocketAddresses = proxySocketAddresses;
    }

    @Override
    public void init() {
        clients = proxySocketAddresses
                .stream()
                .map(proxySocketAddress -> new InetSocketAddress(proxySocketAddress.getHost(), proxySocketAddress.getPort()))
                .map(address -> new Proxy(Proxy.Type.HTTP, address))
                .map(proxy -> okHttpClient.newBuilder().proxy(proxy).build())
                .map(SimpleClient::new).collect(Collectors.toList());
        clients.add(new SimpleClient(okHttpClient));
    }

    @Override
    public List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate) {
        return getAnySimpleClient().getOneMinuteBars(symbol, beginDate);
    }

    @Override
    public OffsetDateTime getDateOfFirstTrade(String symbol) {
        return getAnySimpleClient().getDateOfFirstTrade(symbol);
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        return getAnySimpleClient().getExchangeInfo();
    }

    @Override
    public Set<String> getAllSymbols() {
        return getAnySimpleClient().getAllSymbols();
    }

    @Override
    public boolean isExhausted() {
        return getAnySimpleClient().isExhausted();
    }

    private SimpleClient getAnySimpleClient() {
        return clients.stream().filter(client -> client.isActive() && !client.isExhausted()).findFirst().get();
    }
}