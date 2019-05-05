package com.birobot.quotes_storage.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Candle {
    private OffsetDateTime openTime;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private OffsetDateTime closeTime;
    private BigDecimal quoteAssetVolume;
    private long numberOfTrades;
    private BigDecimal takerBuyBaseAssetVolume;
    private BigDecimal takerBuyQuoteAssetVolume;

    public OffsetDateTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(OffsetDateTime openTime) {
        this.openTime = openTime;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public OffsetDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(OffsetDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public BigDecimal getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public void setQuoteAssetVolume(BigDecimal quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }

    public long getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(long numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public BigDecimal getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }

    public void setTakerBuyBaseAssetVolume(BigDecimal takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }

    public BigDecimal getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }

    public void setTakerBuyQuoteAssetVolume(BigDecimal takerBuyQuoteAssetVolume) {
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
