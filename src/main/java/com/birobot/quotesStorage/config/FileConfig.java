package com.birobot.quotesStorage.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "config")
public class FileConfig {

    private Set<String> symbols;
    private List<ProxyServer> proxyServers;
    private DbConfig dbConfig;

    @XmlElementWrapper(name = "symbols")
    @XmlElement(name="symbol")
    public Set<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(Set<String> symbols) {
        this.symbols = symbols;
    }

    @XmlElement(name="database")
    public DbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @XmlElementWrapper(name = "proxies")
    @XmlElement(name="proxy")
    public List<ProxyServer> getProxyServers() {
        return proxyServers;
    }

    public void setProxyServers(List<ProxyServer> proxyServers) {
        this.proxyServers = proxyServers;
    }
}
