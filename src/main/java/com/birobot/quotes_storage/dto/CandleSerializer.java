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
        jsonGenerator.writeString(Double.toString(candle.getOpen()));
        jsonGenerator.writeString(Double.toString(candle.getHigh()));
        jsonGenerator.writeString(Double.toString(candle.getLow()));
        jsonGenerator.writeString(Double.toString(candle.getClose()));
        jsonGenerator.writeString(Double.toString(candle.getVolume()));
        jsonGenerator.writeNumber(DateUtil.fromOffsetDateTime(candle.getCloseTime()));
        jsonGenerator.writeString(Double.toString(candle.getQuoteAssetVolume()));
        jsonGenerator.writeNumber(candle.getNumberOfTrades());
        jsonGenerator.writeString(Double.toString(candle.getTakerBuyBaseAssetVolume()));
        jsonGenerator.writeString(Double.toString(candle.getTakerBuyQuoteAssetVolume()));
        jsonGenerator.writeEndArray();
    }
}
