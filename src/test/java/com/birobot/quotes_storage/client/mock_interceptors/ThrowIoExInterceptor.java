package com.birobot.quotes_storage.client.mock_interceptors;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class ThrowIoExInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        throw new IOException("test");
    }
}
