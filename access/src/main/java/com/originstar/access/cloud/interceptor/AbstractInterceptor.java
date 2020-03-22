package com.originstar.access.cloud.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractInterceptor implements Interceptor {

    public Response intercept(Chain chain) throws IOException {
        Request newRequest = interceptRequest(chain.request());
        return proceed(chain, newRequest);
    }

    protected Response proceed(Chain chain, Request request) throws IOException {
        return chain.proceed(request);
    }

    protected abstract Request interceptRequest(Request request);

}
