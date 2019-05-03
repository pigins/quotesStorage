package com.birobot.quotes_storage.download;

class AgentParams {
    private long hoursNextTime;
    private long secondsPeriodAfterEmptyResult;

    AgentParams(long hoursNextTime, long secondsPeriodAfterEmptyResult) {
        this.hoursNextTime = hoursNextTime;
        this.secondsPeriodAfterEmptyResult = secondsPeriodAfterEmptyResult;
    }

    long getHoursNextTime() {
        return hoursNextTime;
    }

    long getSecondsPeriodAfterEmptyResult() {
        return secondsPeriodAfterEmptyResult;
    }
}
