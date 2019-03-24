package com.birobot.quotesStorage;

import com.birobot.quotesStorage.client.Client;
import com.birobot.quotesStorage.client.ProxyClient;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.birobot.quotesStorage.agent.DownloadAgent;
import com.birobot.quotesStorage.client.SimpleClient;
import com.birobot.quotesStorage.config.Config;
import com.birobot.quotesStorage.database.QuotesDatabase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class App {
    private static Logger logger = LogManager.getLogger();
    private Config config;

    App(Config config) {
        this.config = config;
    }

    public static void main(String[] args) {
        logger.info("start application");
        Config config = new Config(args);
        config.init();
        new App(config).start();
    }

    public void start() {
        QuotesDatabase db = initDatabase();
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
        if (config.getFileConfig().getProxyServers().size() > 0) {
            client = new ProxyClient(okHttpClient, config.getFileConfig().getProxyServers());
        } else {
            client = new SimpleClient(okHttpClient);
        }
        client.init();
        return client;
    }

    public QuotesDatabase initDatabase() {
        QuotesDatabase db = new QuotesDatabase(config.getFileConfig().getDbConfig());
        db.init();
        return db;
    }

    private Set<String> getValidSymbols(Client client) {
        Set<String> allSymbols = client.getAllSymbols();
        Set<String> userSymbols = config.getFileConfig().getSymbols();
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
