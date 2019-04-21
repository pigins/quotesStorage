package com.birobot.quotes_storage.client;

import okhttp3.OkHttpClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.birobot.quotes_storage.dto.ExchangeInfo;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class ClientTest {
    private Client client;

    @BeforeClass
    public void setUp() {
        OkHttpClient okHttpClient = (new okhttp3.OkHttpClient.Builder()).pingInterval(20L, TimeUnit.SECONDS).build();
        client = new SimpleClient(okHttpClient);
        client.init();
    }

    @Test
    public void testGetExchangeInfo() {
        ExchangeInfo exchangeInfo = client.getExchangeInfo();
        assertNotNull(exchangeInfo);
    }

    @Test
    public void testGetAllSymbols() {
        assertTrue(!client.getAllSymbols().isEmpty());
    }

    @Test
    public void testFindDateOfFirstTrade() {
        assertEquals(OffsetDateTime.parse("2017-07-01T00:00Z"), client.getDateOfFirstTrade("ETHBTC"));
    }

    @Test
    public void delisted() {
        String delistedPair = "BCNETH";
        System.out.println(client.getExchangeInfo().getSymbols().stream().filter(s -> s.getSymbol().equals("BCNETH")).findFirst().get().getStatus());
    }
}