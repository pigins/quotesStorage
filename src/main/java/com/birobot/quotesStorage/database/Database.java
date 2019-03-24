package com.birobot.quotesStorage.database;

import com.birobot.quotesStorage.config.DbConfig;
import com.birobot.quotesStorage.database.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hsqldb.jdbc.JDBCPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static Logger logger = LogManager.getLogger();

    private DataSource dataSource;
    private DbConfig dbConfig;

    public Database(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public void init() {
        try {
            createDatasource();
            if (original()) {
                createStructure();
            }
            addShutdownHook();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public Query query() {
        return new Query(dataSource);
    }

    public boolean tableExist(String tableName) {
        try {
            Connection conn = dataSource.getConnection();
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

    private void createDatasource() {
        String url = String.format("jdbc:hsqldb:file:/%s", dbConfig.getDir().toString());
        JDBCPool dataSource = new JDBCPool(dbConfig.getConnectionPoolSize());
        dataSource.setUrl(url);
        dataSource.setUser("SA");
        dataSource.setPassword("");
        this.dataSource = dataSource;
    }

    private boolean original() {
        return query()
                .sql(String.format(
                        "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'",
                        dbConfig.getSchemaName()
                ))
                .empty();
    }

    private void createStructure() {
        query().sql(String.format("ALTER SCHEMA PUBLIC RENAME TO %s", dbConfig.getSchemaName())).execute();
    }

    private void addShutdownHook() {
        Thread hook = new Thread(() -> query().sql("SHUTDOWN").execute());
        Runtime.getRuntime().addShutdownHook(hook);
    }
}
