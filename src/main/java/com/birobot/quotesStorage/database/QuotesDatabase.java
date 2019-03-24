package com.birobot.quotesStorage.database;

import com.birobot.quotesStorage.config.DbConfig;
import com.birobot.quotesStorage.client.dto.Candle;

import java.time.OffsetDateTime;
import java.util.List;

public class QuotesDatabase extends Database {

    public QuotesDatabase(DbConfig dbConfig) {
        super(dbConfig);
    }

    public void createQuotesTable(String tableName) {
        String sb = String.format("CREATE TABLE %s (", tableName) +
                "  open_time                TIMESTAMP WITH TIME ZONE PRIMARY KEY," +
                "  open                     DOUBLE NOT NULL," +
                "  high                     DOUBLE NOT NULL," +
                "  low                      DOUBLE NOT NULL," +
                "  close                    DOUBLE NOT NULL," +
                "  volume                   DOUBLE NOT NULL," +
                "  close_time               TIMESTAMP WITH TIME ZONE NOT NULL," +
                "  quote_asset_volume       DOUBLE NOT NULL," +
                "  number_of_trades         INT    NOT NULL," +
                "  takerBuyBaseAssetVolume  DOUBLE NOT NULL," +
                "  takerBuyQuoteAssetVolume DOUBLE NOT NULL )";
        query().sql(sb).execute();
        query().sql("CREATE INDEX " + tableName + "_CLOSE_TIME ON " + tableName + "(CLOSE_TIME)").execute();
    }

    public void insertQuotes(String currencyPair, List<Candle> quotes) {
        quotes.forEach(candlestick -> {
            String query =
                    "INSERT INTO " + currencyPair + "(" +
                            "open_time," +
                            " open," +
                            " high," +
                            " low," +
                            " close," +
                            " volume," +
                            " close_time," +
                            " quote_asset_volume," +
                            " number_of_trades," +
                            " takerBuyBaseAssetVolume,"  +
                            " takerBuyQuoteAssetVolume) " +
                            "VALUES(" +
                            "?," +
                            candlestick.getOpen() + "," +
                            candlestick.getHigh() + "," +
                            candlestick.getLow() + "," +
                            candlestick.getClose() + "," +
                            candlestick.getVolume() + "," +
                            "?," +
                            candlestick.getQuoteAssetVolume() + "," +
                            candlestick.getNumberOfTrades() + "," +
                            candlestick.getTakerBuyBaseAssetVolume() + "," +
                            candlestick.getTakerBuyQuoteAssetVolume() + ")";
            query().sql(query)
                    .param(candlestick.getOpenTime())
                    .param(candlestick.getCloseTime())
                    .execute();
        });
    }

    public void createQuotesTableIfNotExist(String tableName) {
        if (!tableExist(tableName)) {
            createQuotesTable(tableName);
        }
    }

    public OffsetDateTime getLatestCloseDate(String tableName) {
        return query().sql("SELECT MAX(CLOSE_TIME) FROM " + tableName).singleDate();
    }
}
