package com.oris.access.cloud.convert;

import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2017/4/30/030.
 */

public class RequestConvert implements Converter<RequestBody, RequestBody> {
    private static final String TAG = "RequestConvert";
    @Override
    public RequestBody convert(RequestBody value) throws IOException {
        return value;
    }
}
