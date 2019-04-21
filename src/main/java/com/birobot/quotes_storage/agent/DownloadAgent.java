package com.birobot.quotes_storage.agent;

import com.birobot.quotes_storage.client.Client;
import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.database.QuotesDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.List;

public class DownloadAgent {
    private static Logger logger = LogManager.getLogger();
    private final String symbol;
    private final QuotesDatabase database;
    private final Client client;
    private OffsetDateTime latestClose;
    private boolean stop = false;

    public DownloadAgent(String currencyPair, QuotesDatabase database, Client client) {
        this.symbol = currencyPair;
        this.database = database;
        this.client = client;
    }

    public void init() {
        database.createQuotesTableIfNotExist(symbol);
        OffsetDateTime beginDate = database.getLatestCloseDate(symbol);
        if (beginDate == null) {
            beginDate = client.getDateOfFirstTrade(symbol);
        }
        latestClose = beginDate;
    }

    public void downloadNext() {
        List<Candle> oneMinuteBars = client.getOneMinuteBars(symbol, latestClose);
        logger.info("received {} bars for {} and latest close {} ", oneMinuteBars.size(), symbol, latestClose);
        if (oneMinuteBars.size() != 0) {
            database.insertQuotes(symbol, oneMinuteBars);
            Candle lastCandle = oneMinuteBars.get(oneMinuteBars.size() - 1);
            latestClose = lastCandle.getCloseTime();
        } else {
            stop = true;
        }
    }

    public boolean needNext() {
        OffsetDateTime current = OffsetDateTime.now();
        return !stop && current.minusHours(15).isAfter(latestClose);
    }
}
