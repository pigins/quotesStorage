package com.birobot.quotes_storage.client.dto;

import java.time.*;

public class Candle {
    private OffsetDateTime openTime;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private OffsetDateTime closeTime;
    private double quoteAssetVolume;
    private long numberOfTrades;
    private double takerBuyBaseAssetVolume;
    private double takerBuyQuoteAssetVolume;

    public OffsetDateTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(OffsetDateTime openTime) {
        this.openTime = openTime;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public OffsetDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(OffsetDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public double getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public void setQuoteAssetVolume(double quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }

    public long getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(long numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public double getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }

    public void setTakerBuyBaseAssetVolume(double takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }

    public double getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }

    public void setTakerBuyQuoteAssetVolume(double takerBuyQuoteAssetVolume) {
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }

    @Override
    public String toString() {
        return "Candle{" +
                "openTime=" + getOpenTime() +
                ", open='" + getOpen() + '\'' +
                ", high='" + getHigh() + '\'' +
                ", low='" + getLow() + '\'' +
                ", close='" + getClose() + '\'' +
                ", volume='" + getVolume() + '\'' +
                ", closeTime=" + getCloseTime() +
                ", quoteAssetVolume='" + getQuoteAssetVolume() + '\'' +
                ", numberOfTrades=" + getNumberOfTrades() +
                ", takerBuyBaseAssetVolume='" + getTakerBuyBaseAssetVolume() + '\'' +
                ", takerBuyQuoteAssetVolume='" + getTakerBuyQuoteAssetVolume() + '\'' +
                '}';
    }

}
