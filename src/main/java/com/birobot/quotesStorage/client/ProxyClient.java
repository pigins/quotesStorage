package com.birobot.quotesStorage.client;

import okhttp3.OkHttpClient;
import com.birobot.quotesStorage.client.dto.Candle;
import com.birobot.quotesStorage.client.dto.ExchangeInfo;
import com.birobot.quotesStorage.config.ProxyServer;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProxyClient implements Client {
    private final OkHttpClient okHttpClient;
    private final List<ProxyServer> proxyServers;
    private List<SimpleClient> clients;

    public ProxyClient(OkHttpClient okHttpClient, List<ProxyServer> proxyServers) {
        this.okHttpClient = okHttpClient;
        this.proxyServers = proxyServers;
    }

    @Override
    public void init() {
        clients = proxyServers
                .stream()
                .map(proxyServer -> new InetSocketAddress(proxyServer.getAddress(), proxyServer.getPort()))
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
