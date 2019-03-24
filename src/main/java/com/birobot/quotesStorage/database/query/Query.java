package com.birobot.quotesStorage.database.query;

import javax.sql.DataSource;
import java.util.Objects;

public class Query {
    DataSource dataSource;

    public Query(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }

    public SqlQuery sql(String sql) {
        Objects.requireNonNull(sql);
        return new SqlQuery(this, sql);
    }
}
