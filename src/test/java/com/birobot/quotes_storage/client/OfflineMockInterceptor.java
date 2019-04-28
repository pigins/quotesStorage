package com.birobot.quotes_storage.client;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class OfflineMockInterceptor implements Interceptor {

    private static final MediaType MEDIA_JSON = MediaType.parse("application/json");
    private final int code;
    private final String body;
    private Map<String, String> headers;

    public OfflineMockInterceptor(int code, String jsonBody, Map<String, String> headers) {
        this.code = code;
        this.body = jsonBody;
        this.headers = headers;
    }

    public OfflineMockInterceptor(int code, String jsonBody) {
        this.code = code;
        this.body = jsonBody;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response.Builder builder = new Response.Builder()
                .body(ResponseBody.create(MEDIA_JSON, body))
                .request(chain.request())
                .message("foo")
                .protocol(Protocol.HTTP_2)
                .code(this.code);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }
        return builder.build();
    }
}
