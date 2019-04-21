package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.dto.CandleDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;

@Component
public class ClientObjectMapper extends ObjectMapper {

    public ClientObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Candle.class, new CandleDeserializer());
        this.registerModule(module);
    }
}
