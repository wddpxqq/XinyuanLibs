package com.originstar.access.cloud.client;

import com.originstar.access.cloud.convert.IResponseConvertBuilder;
import com.originstar.access.cloud.ssl.SSLFactoryUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;

public abstract class ClientConfiguration {
    public abstract String getBaseUrl();
    public abstract long getWriteTimeOut();
    public abstract long getConnectTimeOut();
    public abstract long getReadTimeOut();
    public abstract TimeUnit getTimeUnit();
    public SSLSocketFactory getSSLFactory(){
        return SSLFactoryUtils.getSSLFactory();
    }
    public X509TrustManager getTrustManager(){
        return SSLFactoryUtils.getTrustManager();
    }
    public IResponseConvertBuilder getResponseConvert(){
        return null;
    }
    public Interceptor getParamsInterceptor(){
        return null;
    }
}
