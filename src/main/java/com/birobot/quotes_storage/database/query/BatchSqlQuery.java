package com.birobot.quotes_storage.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BatchSqlQuery {
    private final Query query;
    private final String sql;
    private final int chunkSize;
    private List<Object[]> params = new ArrayList<>();

    public BatchSqlQuery(Query query, String sql, int chunkSize) {
        this.query = query;
        this.sql = sql;
        this.chunkSize = chunkSize;
    }

    public BatchSqlQuery(Query query, String sql) {
        this(query, sql, 100);
    }

    public void params(Object... params) {
        this.params.add(params);
    }

    public void execute() {
        try (Connection conn = this.query.dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
//            conn.setAutoCommit(false);
            for (int i = 0; i < params.size(); i++) {
                for (int j = 0; j < params.get(i).length; j++) {
                    preparedStatement.setObject(j+1, params.get(i)[j]);
                }
                preparedStatement.addBatch();
                if (i > 0 && (i % chunkSize) == 0) {
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch();
//            conn.commit();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
}
