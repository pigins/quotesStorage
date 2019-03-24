package com.birobot.quotesStorage.config;

import javax.xml.bind.annotation.XmlAttribute;

public class ProxyServer {
    private String address;
    private int port;

    public ProxyServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public ProxyServer() {
    }

    @XmlAttribute
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlAttribute
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
