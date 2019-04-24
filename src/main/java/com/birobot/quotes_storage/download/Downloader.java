package com.birobot.quotes_storage.download;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.config.ClientConfig;
import com.birobot.quotes_storage.database.QuotesDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class Downloader {
    private static Logger logger = LogManager.getLogger();

    private final QuotesDatabase db;
    private final Client client;
    private final ClientConfig clientConfig;

    @Autowired
    public Downloader(QuotesDatabase db, Client client, ClientConfig clientConfig) {
        this.db = db;
        this.client = client;
        this.clientConfig = clientConfig;
    }

    @PostConstruct
    public void run() {
        List<Agent> agents = initDownloadAgents(db, client);
        runAgents(agents);
    }

    private void runAgents(List<Agent> agents) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
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

    private List<Agent> initDownloadAgents(QuotesDatabase db, Client client) {
        Set<String> validSymbols = getValidSymbols(client);
        List<Agent> agents = validSymbols
                .stream()
                .map(symbol -> new Agent(symbol, db, client))
                .collect(Collectors.toList());
        agents.forEach(Agent::init);
        return agents;
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