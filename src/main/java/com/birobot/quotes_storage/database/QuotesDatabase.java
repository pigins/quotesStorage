package com.birobot.quotes_storage.database;

import com.birobot.quotes_storage.config.DatabaseConfig;
import com.birobot.quotes_storage.dto.Candle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hsqldb.jdbc.JDBCPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class QuotesDatabase {
    private static Logger logger = LogManager.getLogger();

    private final DatabaseConfig dbConfig;
    private JdbcTemplate template;

    @Autowired
    public QuotesDatabase(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @PostConstruct
    public void init() {
        createTemplate();
        try {
            if (original()) {
                createStructure();
            }
            addShutdownHook();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void insertQuotes(String currencyPair, List<Candle> quotes) {
        template.batchUpdate("INSERT INTO " + currencyPair +
                "(open_time," +
                " open," +
                " high," +
                " low," +
                " close," +
                " volume," +
                " close_time," +
                " quote_asset_volume," +
                " number_of_trades," +
                " takerBuyBaseAssetVolume," +
                " takerBuyQuoteAssetVolume) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, quotes.get(i).getOpenTime());
                ps.setDouble(2, quotes.get(i).getOpen());
                ps.setDouble(3, quotes.get(i).getHigh());
                ps.setDouble(4, quotes.get(i).getLow());
                ps.setDouble(5, quotes.get(i).getClose());
                ps.setDouble(6, quotes.get(i).getVolume());
                ps.setObject(7, quotes.get(i).getCloseTime());
                ps.setDouble(8, quotes.get(i).getQuoteAssetVolume());
                ps.setInt(9, (int) quotes.get(i).getNumberOfTrades());
                ps.setDouble(10, quotes.get(i).getTakerBuyBaseAssetVolume());
                ps.setDouble(11, quotes.get(i).getTakerBuyQuoteAssetVolume());
            }

            @Override
            public int getBatchSize() {
                return Math.min(100, quotes.size());
            }
        });
    }

    public void createQuotesTableIfNotExist(String tableName) {
        if (!tableExist(tableName)) {
            createQuotesTable(tableName);
        }
    }

    public OffsetDateTime getLatestCloseDate(String tableName) {
        return template.queryForObject(
                "SELECT MAX(CLOSE_TIME) FROM " + tableName, OffsetDateTime.class);
    }

    public List<String> getAllQuoteTableNames() {
        return template.queryForList(String.format(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '%s'",
                dbConfig.getSchemaName()), String.class
        );
    }

    public List<Candle> getKlines(String symbol, OffsetDateTime begin, OffsetDateTime end, Integer limit) {
        RowMapper<Candle> candleRowMapper = (rs, i) -> {
            Candle candle = new Candle();
            candle.setOpenTime((OffsetDateTime) rs.getObject(1));
            candle.setOpen(rs.getDouble(2));
            candle.setHigh(rs.getDouble(3));
            candle.setLow(rs.getDouble(4));
            candle.setClose(rs.getDouble(5));
            candle.setVolume(rs.getDouble(6));
            candle.setCloseTime((OffsetDateTime) rs.getObject(7));
            candle.setQuoteAssetVolume(rs.getDouble(8));
            candle.setNumberOfTrades(rs.getInt(9));
            candle.setTakerBuyBaseAssetVolume(rs.getDouble(10));
            candle.setTakerBuyQuoteAssetVolume(rs.getDouble(11));
            return candle;
        };
        if (begin == null && end == null) {
            return template.query(
                    String.format("SELECT * FROM %s ORDER BY OPEN_TIME DESC LIMIT ?", symbol),
                    preparedStatement -> preparedStatement.setInt(1, limit), candleRowMapper
            );
        } else if (begin != null && end == null) {
            return template.query(
                    String.format("SELECT * FROM %s WHERE OPEN_TIME >= ? ORDER BY OPEN_TIME DESC LIMIT ?", symbol),
                    preparedStatement -> {
                        preparedStatement.setObject(1, begin);
                        preparedStatement.setInt(2, limit);
                    }, candleRowMapper);
        } else if (begin == null) {
            return template.query(
                    String.format("SELECT * FROM %s WHERE CLOSE_TIME <= ? ORDER BY OPEN_TIME DESC LIMIT ?", symbol),
                    preparedStatement -> {
                        preparedStatement.setObject(1, end);
                        preparedStatement.setInt(2, limit);
                    }, candleRowMapper);
        } else {
            return template.query(
                    String.format("SELECT * FROM %s WHERE OPEN_TIME >= ? AND CLOSE_TIME <= ? ORDER BY OPEN_TIME DESC LIMIT ?", symbol),
                    preparedStatement -> {
                        preparedStatement.setObject(1, begin);
                        preparedStatement.setObject(2, end);
                        preparedStatement.setInt(3, limit);
                    }, candleRowMapper);
        }
    }

    public boolean symbolExist(String symbol) {
        return tableExist(symbol);
    }

    private boolean tableExist(String tableName) {
        try {
            Connection conn = template.getDataSource().getConnection();
            boolean tExists = false;
            try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
                while (rs.next()) {
                    String tName = rs.getString("TABLE_NAME");
                    if (tName != null && tName.equals(tableName)) {
                        tExists = true;
                        break;
                    }
                }
            }
            return tExists;
        } catch (SQLException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private void createQuotesTable(String tableName) {
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
        template.execute(sb);
        template.execute("CREATE INDEX " + tableName + "_CLOSE_TIME ON " + tableName + "(CLOSE_TIME)");
    }

    private void addShutdownHook() {
        Thread hook = new Thread(() -> template.execute("SHUTDOWN"));
        Runtime.getRuntime().addShutdownHook(hook);
    }

    private boolean original() {
        return template.queryForList(String.format(
                "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'",
                dbConfig.getSchemaName()), String.class
        ).isEmpty();
    }

    private void createStructure() {
        template.execute(String.format("ALTER SCHEMA PUBLIC RENAME TO %s", dbConfig.getSchemaName()));
    }

    private void createTemplate() {
        String url = String.format("jdbc:hsqldb:file:/%s", Paths.get(dbConfig.getPath()).resolve("db").toString());
        JDBCPool dataSource = new JDBCPool(100);
        dataSource.setUrl(url);
        dataSource.setUser("SA");
        dataSource.setPassword("");

        template = new JdbcTemplate(dataSource);
    }
}
