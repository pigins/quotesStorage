package com.birobot.quotes_storage;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

public class DateUtil {

    public static OffsetDateTime toOffsetDateTime(long millis) {
        return OffsetDateTime.of(LocalDateTime.ofEpochSecond(
                millis / 1000,
                (int)(millis % 1000) * 1_000_000, ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static Long fromOffsetDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime.getLong(ChronoField.MILLI_OF_SECOND);
    }
}
