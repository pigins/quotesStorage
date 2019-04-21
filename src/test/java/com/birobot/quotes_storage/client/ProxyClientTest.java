package com.birobot.quotes_storage.client;

import okhttp3.OkHttpClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.birobot.quotes_storage.config.ProxySocketAddress;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class ProxyClientTest {
    private Client client;

    @BeforeClass
    public void setUp() {
        OkHttpClient okHttpClient = (new okhttp3.OkHttpClient.Builder()).pingInterval(20L, TimeUnit.SECONDS).build();
        ProxySocketAddress proxySocketAddress = new ProxySocketAddress("190.8.168.252", 8080);
        client = new ProxyClient(okHttpClient, List.of(proxySocketAddress));
        client.init();
    }

    @Test
    public void testGetResponse() {
        assertNotNull(client.getExchangeInfo());
    }
}