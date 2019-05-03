package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.ObjectMapperConfig;
import com.birobot.quotes_storage.client.mock_interceptors.OfflineMockInterceptor;
import com.birobot.quotes_storage.client.mock_interceptors.ThrowIoExInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@SpringBootTest(classes = ObjectMapperConfig.class)
public class ProxyClientTest extends AbstractTestNGSpringContextTests {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testUseAnyAvailableClient() {
        // если клиент выбросил exception патаемся взять другой
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ThrowIoExInterceptor())
                .build();
        SimpleClient invalidClient = new SimpleClient(okHttpClient, objectMapper);
        OkHttpClient okHttpClient1 = new OkHttpClient.Builder()
                .addInterceptor(new OfflineMockInterceptor(200, "[]"))
                .build();
        SimpleClient okClient = new SimpleClient(okHttpClient1, objectMapper);

        Client client = new ProxyClient(okHttpClient, List.of(), objectMapper);

        List<SimpleClient> clients = List.of(invalidClient, okClient);
        ReflectionTestUtils.setField(client, "clients", clients);
        Assert.assertThrows(RuntimeException.class, client::getExchangeInfo);
        Assert.assertFalse(invalidClient.isAvailable());
        Assert.assertTrue(okClient.isAvailable());
    }
}