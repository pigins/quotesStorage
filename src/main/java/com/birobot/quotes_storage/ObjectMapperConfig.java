package com.birobot.quotes_storage;

import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.dto.CandleDeserializer;
import com.birobot.quotes_storage.dto.CandleSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootConfiguration
public class ObjectMapperConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Candle.class, new CandleDeserializer());
        module.addSerializer(new CandleSerializer(Candle.class));
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
