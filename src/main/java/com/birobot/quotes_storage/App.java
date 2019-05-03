package com.birobot.quotes_storage;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.client.ProxyClient;
import com.birobot.quotes_storage.client.SimpleClient;
import com.birobot.quotes_storage.config.ClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class App {
    private static Logger logger = LogManager.getLogger();

    private final ClientConfig clientConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public App(ClientConfig clientConfig, ObjectMapper objectMapper) {
        this.clientConfig = clientConfig;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public Client getClient() {
        OkHttpClient okHttpClient = (new OkHttpClient.Builder()).pingInterval(20L, TimeUnit.SECONDS).build();
        if (clientConfig.getType() == null || clientConfig.getType().equalsIgnoreCase("simple")) {
            logger.info("init simple client");
            return new SimpleClient(okHttpClient, objectMapper);
        } else if (clientConfig.getType().equalsIgnoreCase("proxy")) {
            logger.info("init proxy client");
            return new ProxyClient(okHttpClient, this.clientConfig.getProxies(), objectMapper);
        } else {
            throw new IllegalStateException(
                    "pass client type parameter to application.yml. Possible values: \"simple\" or \"proxy\""
            );
        }
    }
}
