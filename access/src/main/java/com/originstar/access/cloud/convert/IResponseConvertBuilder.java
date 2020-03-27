package com.originstar.access.cloud.convert;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public interface IResponseConvertBuilder<T> {
    Converter<ResponseBody,T> build(Type type);
}
