package com.oris.access.cloud.convert;

import com.alibaba.fastjson.JSON;
import com.oris.olog.OLog;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class BaseResponseCovert<T> implements Converter<ResponseBody,T>
        ,IResponseConvertBuilder<T>{
    private String TAG = "BaseResponseCovert";
    protected final Type type;

    protected BaseResponseCovert(){
        type = null;
    }

    protected BaseResponseCovert(Type type){
        this.type = type;
    }

    public T convert(ResponseBody response) throws IOException {
        String content = response.string();
        if (content != null) {
            OLog.i(TAG, String.format("Received ResponseBody:%n%s", content));
            return convertPlain(content);
        } else {
            OLog.i(TAG, String.format("Received ResponseBody:%n%s", content));
            return convertStream(response);
        }
    }

    @Override
    public Converter<ResponseBody, T> build(Type type) {
        return new BaseResponseCovert(type);
    }

    protected T convertStream(ResponseBody response){
        return null;
    }

    protected T convertPlain(String content){
        return JSON.parseObject(content,type);
    }
}
