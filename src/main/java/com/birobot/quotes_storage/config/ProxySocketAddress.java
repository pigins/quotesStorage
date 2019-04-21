package com.birobot.quotes_storage.config;

public class ProxySocketAddress {
    private String host;
    private int port;

    public ProxySocketAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ProxySocketAddress() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ProxySocketAddress{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
