package com.birobot.quotesStorage.database.query;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlQuery {
    private final Query query;
    private final String sql;

    private List<Object> params;

    SqlQuery(Query query, String sql) {
        this.query = query;
        this.sql = sql;
    }

    public boolean empty() {
        try (Connection conn = this.query.dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)
        ) {
            return !resultSet.next();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public boolean notEmpty() {
        return !empty();
    }

    public <T> List<T> select(Rows<T> rows) {
        try (Connection conn = this.query.dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)
        ) {
            return rows.map(resultSet);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public <T> List<T> select(Row<T> row) {
        try (Connection conn = this.query.dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)
        ) {
            List<T> res = new ArrayList<>();
            while (resultSet.next()) {
                res.add(row.mapRow(resultSet));
            }
            return res;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public SqlQuery param(Object value) {
        if (params == null) {
            params = new ArrayList<>();
        }
        params.add(value);
        return this;
    }

    public void execute() {
        if (params == null) {
            try (Connection conn = this.query.dataSource.getConnection();
                 Statement stmt = conn.createStatement()
            ) {
                stmt.execute(sql);
            } catch (SQLException e) {
                throw new QueryException(e);
            }
        } else {
            try (Connection conn = this.query.dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new QueryException(e);
            }
        }
    }

    public OffsetDateTime singleDate() {
        try (Connection conn = this.query.dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)
        ) {
            resultSet.next();
            return (OffsetDateTime) resultSet.getObject(1);

        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public BatchSqlQuery batch() {
        return new BatchSqlQuery(this.query, this.sql);
    }

    public BatchSqlQuery batch(int chunkSize) {
        return new BatchSqlQuery(this.query, this.sql, chunkSize);
    }
}
