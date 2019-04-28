package com.birobot.quotes_storage.download;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.database.QuotesDatabase;
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
    private OffsetDateTime latestClose;
    private boolean symbolDelisted = false;
    private OffsetDateTime dateOfLastTrade;
    private OffsetDateTime continueDate = OffsetDateTime.MIN;

    Agent(String symbol, QuotesDatabase database, Client client) {
        this.symbol = symbol;
        this.database = database;
        this.client = client;
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
                return now.minusMinutes(15).isAfter(latestClose);
//                return now.minusHours(15).isAfter(latestClose);
            }
        }
        return false;
    }

    void downloadNext() {
        List<Candle> oneMinuteBars = client.getOneMinuteBars(symbol, latestClose);
        logger.info("received {} bars for {} and latest close {} ", oneMinuteBars.size(), symbol, latestClose);
        if (oneMinuteBars.size() != 0) {
            database.insertQuotes(symbol, oneMinuteBars);
            Candle lastCandle = oneMinuteBars.get(oneMinuteBars.size() - 1);
            latestClose = lastCandle.getCloseTime();
        } else {
            continueDate = OffsetDateTime.now().plus(10, ChronoUnit.MINUTES);
        }
    }

    void setSymbolDelisted() {
        this.symbolDelisted = true;
        this.dateOfLastTrade = client.getDateOfLastClose(symbol);
    }

    public String getSymbol() {
        return symbol;
    }
}
