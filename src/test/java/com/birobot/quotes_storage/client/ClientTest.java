package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.ObjectMapperConfig;
import com.birobot.quotes_storage.dto.ExchangeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

@SpringBootTest(classes = ObjectMapperConfig.class)
public class ClientTest extends AbstractTestNGSpringContextTests {
    private Client client;

    @Autowired ObjectMapper objectMapper;

    @BeforeMethod
    public void setUp() {
        System.out.println(objectMapper);
        OkHttpClient okHttpClient = (new okhttp3.OkHttpClient.Builder()).pingInterval(20L, TimeUnit.SECONDS).build();
        client = new SimpleClient(okHttpClient, objectMapper);
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