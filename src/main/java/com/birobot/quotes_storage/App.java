package com.birobot.quotes_storage;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.client.ProxyClient;
import com.birobot.quotes_storage.config.DatabaseConfig;
import com.birobot.quotes_storage.config.ClientConfig;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.birobot.quotes_storage.agent.DownloadAgent;
import com.birobot.quotes_storage.client.SimpleClient;
import com.birobot.quotes_storage.database.QuotesDatabase;
import org.hsqldb.jdbc.JDBCPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
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
    private final QuotesDatabase db;

    @Autowired
    public App(ClientConfig clientConfig, QuotesDatabase db) {
        this.clientConfig = clientConfig;
        this.db = db;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    public void run() {
        Client client = initClient();
        List<DownloadAgent> agents = initDownloadAgents(db, client);
        runAgents(agents);
    }

    public void runAgents(List<DownloadAgent> agents) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
        executor.scheduleAtFixedRate(() -> agents.forEach(agent -> {
            while (agent.needNext()) {
                try {
                    agent.downloadNext();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }), 0, 10, TimeUnit.MINUTES);
    }

    public List<DownloadAgent> initDownloadAgents(QuotesDatabase db, Client client) {
        Set<String> validSymbols = getValidSymbols(client);
        List<DownloadAgent> agents = validSymbols
                .stream()
                .map(symbol -> new DownloadAgent(symbol, db, client))
                .collect(Collectors.toList());
        agents.forEach(DownloadAgent::init);
        return agents;
    }

    public Client initClient() {
        Client client;
        OkHttpClient okHttpClient = (new OkHttpClient.Builder()).pingInterval(20L, TimeUnit.SECONDS).build();
        if (clientConfig.getProxies().size() > 0) {
            client = new ProxyClient(okHttpClient, clientConfig.getProxies());
        } else {
            client = new SimpleClient(okHttpClient);
        }
        client.init();
        return client;
    }

    private Set<String> getValidSymbols(Client client) {
        Set<String> allSymbols = client.getAllSymbols();
        Set<String> userSymbols = clientConfig.getSymbols();
        Set<String> diff = new HashSet<>(userSymbols);
        diff.removeAll(allSymbols);
        if (diff.size() == userSymbols.size()) {
            logger.error("no valid symbols found, exit app. ValidSymbols = " + allSymbols);
            System.exit(1);
        } else if (diff.size() > 0) {
            logger.warn("symbols:" + diff + "are invalid! ValidSymbols = " + allSymbols);
        }
        userSymbols.removeAll(diff);
        return userSymbols;
    }
}
