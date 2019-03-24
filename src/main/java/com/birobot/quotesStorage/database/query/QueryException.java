package com.birobot.quotesStorage.database.query;

public class QueryException extends RuntimeException {
    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String s) {
        super(s);
    }
}
