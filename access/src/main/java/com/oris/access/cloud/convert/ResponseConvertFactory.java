package com.oris.access.cloud.convert;

import android.util.Log;

import com.oris.access.cloud.client.ClientConfiguration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/4/24/024.
 */
public class ResponseConvertFactory extends Factory   {
    IResponseConvertBuilder builder;
   ;
    private static final String TAG = "ResponseConvertFactory";

    public static Factory create( ) {
        return new ResponseConvertFactory(null);
    }

    private ResponseConvertFactory(ClientConfiguration configuration){
        if(configuration == null || configuration.getResponseConvert() == null){
            builder = new BaseResponseCovert();
        }else {
            builder = configuration.getResponseConvert();
        }
    }

    public static Factory create(ClientConfiguration configuration) {
        ResponseConvertFactory responseConvertFactory = new ResponseConvertFactory(configuration);
        return responseConvertFactory;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
       return new RequestConvert();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        try {
            return builder.build(type);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        return null;
    }
}
