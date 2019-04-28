package com.birobot.quotes_storage.client;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

class RequestLimit {
    private int canTryNextTimeMultiplier = 1;
    private OffsetDateTime canTryNextTimeDate = OffsetDateTime.MIN;
    private OffsetDateTime canUseLimitAgainDate = OffsetDateTime.MIN;
    private String reason;

    OffsetDateTime getCanUseLimitAgainDate() {
        return canUseLimitAgainDate;
    }

    void setNoConnection() {
        if (canTryNextTimeMultiplier < 32) {
            canTryNextTimeMultiplier *= 2;
        }
        canTryNextTimeDate = OffsetDateTime.now().plus(canTryNextTimeMultiplier * 5, ChronoUnit.MINUTES);
    }

    void setOkRequest() {
        canTryNextTimeMultiplier = 1;
    }

    void setExhausted(int secondsBeforeCanContinue) {
        canUseLimitAgainDate = OffsetDateTime.now().plus(secondsBeforeCanContinue, ChronoUnit.SECONDS);
    }

    boolean forbid() {
        OffsetDateTime now = OffsetDateTime.now();
        if (canTryNextTimeDate.isAfter(now)) {
            reason = "wait for reconnect, next try after " + canTryNextTimeDate;
            return true;
        } else if (canUseLimitAgainDate.isAfter(now)) {
            reason = "limit exhausted, continue date is " + canUseLimitAgainDate;
            return true;
        }
        return false;
    }

    String getReason() {
        return reason;
    }
}
