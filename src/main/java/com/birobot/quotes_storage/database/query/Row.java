package com.birobot.quotes_storage.database.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Row<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
