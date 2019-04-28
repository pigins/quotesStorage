package com.birobot.quotes_storage.client;

import com.birobot.quotes_storage.dto.Candle;
import com.birobot.quotes_storage.dto.CandlestickInterval;
import com.birobot.quotes_storage.dto.ExchangeInfo;
import com.birobot.quotes_storage.dto.SymbolInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleClient implements Client {
    private static HttpUrl BASE_URL = new HttpUrl.Builder()
            .scheme("https")
            .host("api.binance.com")
            .addPathSegment("api")
            .addPathSegment("v1")
            .build();
    private final JavaType candleListType;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper mapper;
    private RequestLimit requestLimit;

    public SimpleClient(OkHttpClient okHttpClient, ObjectMapper mapper) {
        this.okHttpClient = okHttpClient;
        this.mapper = mapper;
        requestLimit = new RequestLimit();
        candleListType = mapper.getTypeFactory().constructCollectionType(List.class, Candle.class);
    }

    @Override
    public List<Candle> getOneMinuteBars(String symbol, OffsetDateTime beginDate) {
        return getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE, 1000, beginDate.toEpochSecond() * 1000, null);
    }

    @Override
    public OffsetDateTime getDateOfFirstOpen(String symbol) {
        List<Candle> candlestickBars = getCandlestickBars(symbol, CandlestickInterval.MONTHLY);
        return candlestickBars.get(0).getOpenTime();
    }

    @Override
    public OffsetDateTime getDateOfLastClose(String symbol) {
        List<Candle> candlestickBars = getCandlestickBars(symbol, CandlestickInterval.MONTHLY);
        return candlestickBars.get(candlestickBars.size() - 1).getCloseTime();
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
    public boolean isAvailable() {
        return !requestLimit.forbid();
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

    private String getResponse(String path, Map<String, String> queryParams) {
        if (requestLimit.forbid()) {
            throw new RuntimeException(requestLimit.getReason());
        }
        HttpUrl.Builder urlBuilder = BASE_URL.newBuilder().addPathSegment(path);
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }
        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder().get().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 429 || response.code() == 418) {
                int secondsBeforeCanContinue = Integer.valueOf(Objects.requireNonNull(response.header("Retry-After")));
                requestLimit.setExhausted(secondsBeforeCanContinue);
                String message = String.format(
                        "%s exhausted, continue date is %s",
                        okHttpClient.proxy() != null ? "proxy " + Objects.requireNonNull(okHttpClient.proxy()).address() : "local ip address",
                        requestLimit.getCanUseLimitAgainDate()
                );
                throw new RuntimeException(message);
            }
            String result = Objects.requireNonNull(response.body()).string();
            requestLimit.setOkRequest();
            return result;
        } catch (IOException e) {
            requestLimit.setNoConnection();
            throw new RuntimeException(e);
        }
    }
}
