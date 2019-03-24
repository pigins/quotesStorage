package com.birobot.quotesStorage.client;

import com.birobot.quotesStorage.client.dto.Candle;
import com.birobot.quotesStorage.client.dto.CandlestickInterval;
import com.birobot.quotesStorage.client.dto.ExchangeInfo;
import com.birobot.quotesStorage.client.dto.SymbolInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  * If you use okhttpClient.newBuilder() to create one client from another, then each client will be very lightweight.
 *  They will share the same connection pool and dispatcher, for example. See OkHttpClient’s docs on sharing.
 */
public class SimpleClient implements Client {
    private static Logger logger = LogManager.getLogger();
    protected static HttpUrl BASE_URL = new HttpUrl.Builder()
            .scheme("https")
            .host("api.binance.com")
            .addPathSegment("api")
            .addPathSegment("v1")
            .build();
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Candle.class, new CandleDesirializer());
        mapper.registerModule(module);
    }
    private static JavaType candleListType = mapper.getTypeFactory().constructCollectionType(List.class, Candle.class);

    private OkHttpClient okHttpClient;
    private OffsetDateTime continueDate;
    private OffsetDateTime activeDate;

    public SimpleClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public void init() {
    }

    @Override
    public List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate) {
        return getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE, 1000, beginDate.toEpochSecond() * 1000, null);
    }

    @Override
    public OffsetDateTime getDateOfFirstTrade(String symbol) {
        List<Candle> candlestickBars = getCandlestickBars(symbol, CandlestickInterval.MONTHLY);
        return candlestickBars.get(0).getOpenTime();
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        return getJson("exchangeInfo", null, ExchangeInfo.class);
    }

    @Override
    public Set<String> getAllSymbols() {
        return getExchangeInfo().getSymbols().stream().map(SymbolInfo::getSymbol).collect(Collectors.toSet());
    }

    @Override
    public boolean isExhausted() {
        if (continueDate == null) {
            return false;
        }
        return OffsetDateTime.now().isAfter(continueDate);
    }

    public boolean isActive() {
        if (activeDate == null) {
            return true;
        }
        return OffsetDateTime.now().isAfter(activeDate);
    }

    protected String getResponse(String path, Map<String, String> queryParams) {
        return getResponse(okHttpClient, path, queryParams);
    }

    String getResponse(OkHttpClient client, String path, Map<String, String> queryParams) {
        HttpUrl.Builder urlBuilder = BASE_URL.newBuilder().addPathSegment(path);
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }
        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder().get().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 429 || response.code() == 418) {
                int secondsBeforeCanContinue = Integer.valueOf(Objects.requireNonNull(response.header("Retry-After")));
                continueDate = OffsetDateTime.now().plus(secondsBeforeCanContinue, ChronoUnit.SECONDS);
                logger.info(
                        "{} exhausted, continue date is {}",
                        okHttpClient.proxy() != null ? "proxy " + okHttpClient.proxy().address() : "local ip address",
                        continueDate
                );
            }
            return response.body().string();
        } catch (IOException e) {
            activeDate = OffsetDateTime.now().plus(5, ChronoUnit.MINUTES);
            throw new RuntimeException(e);
        }
    }

    private List<Candle> getCandlestickBars(String symbol, CandlestickInterval candlestickInterval) {
        return getCandlestickBars(symbol, candlestickInterval, null, null, null);
    }

    private List<Candle> getCandlestickBars(String symbol, CandlestickInterval candlestickInterval, Integer limit,
                                                 Long startTime, Long endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("interval", candlestickInterval.getIntervalId());
        if (limit != null) {
            params.put("limit", Integer.toString(limit));
        }
        if (startTime != null) {
            params.put("startTime", Long.toString(startTime));
        }
        if (endTime != null) {
            params.put("endTime", Long.toString(endTime));
        }
        String klines = getResponse("klines", params);
        List<Candle> candles;
        try {
            candles = mapper.readValue(klines, candleListType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return candles;
    }

    private <T> T getJson(String path, Map<String, String> queryParams, Class<T> clazz) {
        String res = getResponse(path, queryParams);
        try {
            return mapper.readValue(res, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
