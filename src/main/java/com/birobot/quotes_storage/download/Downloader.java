package com.birobot.quotes_storage.download;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.config.ClientConfig;
import com.birobot.quotes_storage.database.QuotesDatabase;
import com.birobot.quotes_storage.dto.SymbolInfo;
import com.birobot.quotes_storage.dto.SymbolStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
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
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private List<Agent> agents;

    @Autowired
    public Downloader(QuotesDatabase db, Client client, ClientConfig clientConfig) {
        this.db = db;
        this.client = client;
        this.clientConfig = clientConfig;
    }

    @PostConstruct
    public void run() {
        initAgents();
        runAgents();
        runCheckForDelistedSymbols();
    }

    private void runCheckForDelistedSymbols() {
        executor.scheduleAtFixedRate(this::findDelisted, 0, 1, TimeUnit.HOURS);
    }

    private void runAgents() {
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

    private void initAgents() {
        agents = clientConfig.getSymbols()
                .stream()
                .map(symbol -> new Agent(symbol, db, client, new AgentParams(15, 10 * 60)))
                .collect(Collectors.toList());
        agents.forEach(Agent::init);
        findDelisted();
    }

    private void findDelisted() {
        List<String> delisted = client.getExchangeInfo().getSymbols().stream()
                .filter(
                        symbolInfo -> symbolInfo.getStatus() == SymbolStatus.BREAK &&
                                clientConfig.getSymbols().contains(symbolInfo.getSymbol())
                ).map(SymbolInfo::getSymbol)
                .collect(Collectors.toList());

        agents.forEach(agent -> {
            if (delisted.contains(agent.getSymbol())) {
                agent.setSymbolDelisted();
            }
        });
    }
}