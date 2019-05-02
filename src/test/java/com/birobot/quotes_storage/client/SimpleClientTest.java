package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.ObjectMapperConfig;
import com.birobot.quotes_storage.client.mock_interceptors.OfflineMockInterceptor;
import com.birobot.quotes_storage.client.mock_interceptors.ThrowIoExInterceptor;
import com.birobot.quotes_storage.dto.ExchangeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

@SpringBootTest(classes = ObjectMapperConfig.class)
public class SimpleClientTest extends AbstractTestNGSpringContextTests {
    private Client client;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeMethod
    public void setUp() {
        OkHttpClient okHttpClient = (new okhttp3.OkHttpClient.Builder()).pingInterval(20L, TimeUnit.SECONDS).build();
        client = new SimpleClient(okHttpClient, objectMapper);
    }

    @Test
    public void testExhausted() throws InterruptedException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Retry-After", String.valueOf(2));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new OfflineMockInterceptor(429, "", headers))
                .build();
        client = new SimpleClient(okHttpClient, objectMapper);
        assertThrows(RuntimeException.class, () -> client.getAllSymbols());
        assertFalse(client.isAvailable());
        Thread.sleep(2000);
        assertTrue(client.isAvailable());
    }

    @Test
    public void testNoConnection() throws InterruptedException {
        client = new SimpleClient(new OkHttpClient.Builder().addInterceptor(new ThrowIoExInterceptor()).build(), objectMapper);
        RequestLimit requestLimit = new RequestLimit(1);
        ReflectionTestUtils.setField(client, "requestLimit", requestLimit);
        for (int i = 1; i <= 32; i *= 2) {
            assertThrows(RuntimeException.class, () -> client.getAllSymbols());
            assertFalse(client.isAvailable());
            Thread.sleep(i * 1000);
            assertTrue(client.isAvailable());
        }
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
        assertEquals(OffsetDateTime.parse("2017-07-01T00:00Z"), client.getDateOfFirstOpen("ETHBTC"));
    }

    @Test
    public void delisted() {
        System.out.println(client.getExchangeInfo().getSymbols().stream().filter(s -> s.getSymbol().equals("BCNETH"))
                .findFirst().get().getStatus());
    }
}