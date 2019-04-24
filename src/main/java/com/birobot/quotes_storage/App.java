package com.birobot.quotes_storage;

import com.birobot.quotes_storage.client.CompositeClient;
import com.birobot.quotes_storage.dto.CandleDeserializer;
import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.client.ProxyClient;
import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.config.ClientConfig;
import com.birobot.quotes_storage.dto.CandleSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.birobot.quotes_storage.agent.DownloadAgent;
import com.birobot.quotes_storage.client.SimpleClient;
import com.birobot.quotes_storage.database.QuotesDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        if (clientConfig.getType() == null || clientConfig.getType().equalsIgnoreCase("composite")) {
            logger.info("init composite client");
            return new CompositeClient(okHttpClient, this.clientConfig.getProxies(), objectMapper);
        } else if (clientConfig.getType().equalsIgnoreCase("simple")) {
            logger.info("init simple client");
            return new SimpleClient(okHttpClient, objectMapper);
        } else if (clientConfig.getType().equalsIgnoreCase("proxy")) {
            logger.info("init proxy client");
            return new ProxyClient(okHttpClient, this.clientConfig.getProxies(), objectMapper);
        } else {
            throw new IllegalStateException(
                    "pass client type parameter to application.yml. Possible values: \"composite\", \"simple\" or \"proxy\""
            );
        }
    }
}
