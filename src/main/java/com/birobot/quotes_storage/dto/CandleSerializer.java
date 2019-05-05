package com.birobot.quotes_storage.dto;

import com.birobot.quotes_storage.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CandleSerializer extends StdSerializer<Candle> {
    public CandleSerializer(Class<Candle> t) {
        super(t);
    }

    @Override
    public void serialize(Candle candle, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeNumber(DateUtil.fromOffsetDateTime(candle.getOpenTime()));
        jsonGenerator.writeString(candle.getOpen().toPlainString());
        jsonGenerator.writeString(candle.getHigh().toPlainString());
        jsonGenerator.writeString(candle.getLow().toPlainString());
        jsonGenerator.writeString(candle.getClose().toPlainString());
        jsonGenerator.writeString(candle.getVolume().toPlainString());
        jsonGenerator.writeNumber(DateUtil.fromOffsetDateTime(candle.getCloseTime()));
        jsonGenerator.writeString(candle.getQuoteAssetVolume().toPlainString());
        jsonGenerator.writeNumber(candle.getNumberOfTrades());
        jsonGenerator.writeString(candle.getTakerBuyBaseAssetVolume().toPlainString());
        jsonGenerator.writeString(candle.getTakerBuyQuoteAssetVolume().toPlainString());
        jsonGenerator.writeEndArray();
    }
}
