package com.birobot.quotes_storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@ConfigurationProperties(prefix="client")
public class ClientConfig {
    private String type;
    private List<ProxySocketAddress> proxies;
    private Set<String> symbols;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ProxySocketAddress> getProxies() {
        return proxies;
    }

    public void setProxies(List<ProxySocketAddress> proxies) {
        this.proxies = proxies;
    }

    public Set<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(Set<String> symbols) {
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "proxies=" + proxies +
                ", symbols=" + symbols +
                '}';
    }
}
