package com.birobot.quotes_storage.download;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.database.QuotesDatabase;
import com.birobot.quotes_storage.dto.Candle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

class Agent {
    private static Logger logger = LogManager.getLogger();
    private final String symbol;
    private final QuotesDatabase database;
    private final Client client;
    private final AgentParams agentParams;
    private OffsetDateTime latestClose;
    private boolean symbolDelisted = false;
    private OffsetDateTime dateOfLastTrade;
    private OffsetDateTime continueDate = OffsetDateTime.MIN;

    Agent(String symbol, QuotesDatabase database, Client client, AgentParams agentParams) {
        this.symbol = symbol;
        this.database = database;
        this.client = client;
        this.agentParams = agentParams;
    }

    void init() {
        OffsetDateTime beginDate;
        if (!database.symbolExist(symbol)) {
            beginDate = client.getDateOfFirstOpen(symbol);
            database.createQuotesTableIfNotExist(symbol);
        } else {
            beginDate = database.getLatestCloseDate(symbol);
        }
        latestClose = beginDate;
    }

    boolean needNext() {
        OffsetDateTime now = OffsetDateTime.now();
        if (client.isAvailable() && now.isAfter(continueDate)) {
            if (symbolDelisted && latestClose.isBefore(dateOfLastTrade)) {
                return true;
            }
            if (!symbolDelisted) {
                return now.minusHours(agentParams.getHoursNextTime()).isAfter(latestClose);
            }
        }
        return false;
    }

    void setSymbolDelisted() {
        this.dateOfLastTrade = client.getDateOfLastClose(symbol);
        this.symbolDelisted = true;
    }

    void downloadNext() {
        List<Candle> oneMinuteBars = client.getOneMinuteBars(symbol, latestClose);
        logger.info("received {} bars for {} and latest close {} ", oneMinuteBars.size(), symbol, latestClose);
        if (oneMinuteBars.size() != 0) {
            database.insertQuotes(symbol, oneMinuteBars);
            Candle lastCandle = oneMinuteBars.get(oneMinuteBars.size() - 1);
            latestClose = lastCandle.getCloseTime();
        } else {
            continueDate = OffsetDateTime.now().plus(agentParams.getSecondsPeriodAfterEmptyResult(), ChronoUnit.SECONDS);
        }
    }

    public String getSymbol() {
        return symbol;
    }
}
