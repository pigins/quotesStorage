package com.birobot.quotesStorage.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.birobot.quotesStorage.client.dto.Candle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CandleDesirializer extends JsonDeserializer<Candle> {

    @Override
    public Candle deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isArray()) {
            Candle candle = new Candle();
            candle.setOpenTime(toOffsetDateTime(node.get(0).asLong()));
            candle.setOpen(Double.valueOf(node.get(1).asText()));
            candle.setHigh(Double.valueOf(node.get(2).asText()));
            candle.setLow(Double.valueOf(node.get(3).asText()));
            candle.setClose(Double.valueOf(node.get(4).asText()));
            candle.setVolume(Double.valueOf(node.get(5).asText()));
            candle.setCloseTime(toOffsetDateTime(node.get(6).asLong()));
            candle.setQuoteAssetVolume(Double.valueOf(node.get(7).asText()));
            candle.setNumberOfTrades(node.get(8).asLong());
            candle.setTakerBuyBaseAssetVolume(Double.valueOf(node.get(9).asText()));
            candle.setTakerBuyQuoteAssetVolume(Double.valueOf(node.get(10).asText()));
            return candle;
        } else return null;
    }

    private OffsetDateTime toOffsetDateTime(long millis) {
        return OffsetDateTime.of(LocalDateTime.ofEpochSecond(
                millis / 1000,
                (int)(millis % 1000) * 1_000_000, ZoneOffset.UTC), ZoneOffset.UTC);
    }

}
