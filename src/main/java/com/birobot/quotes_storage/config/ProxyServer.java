package com.birobot.quotes_storage.config;

public class ProxyServer {
    private String host;
    private int port;

    public ProxyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ProxyServer() {
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
        return "ProxyServer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
