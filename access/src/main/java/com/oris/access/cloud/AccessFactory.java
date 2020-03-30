package com.oris.access.cloud;

import com.oris.access.cloud.client.ClientConfiguration;
import com.oris.access.cloud.convert.ResponseConvertFactory;
import com.oris.access.cloud.ssl.SSLFactoryUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Author:
 * Time: 2016/6/23
 * Desc:云服务管理器
 */
public class AccessFactory<I> {
    private I service;
    private ClientConfiguration configuration;
    private OkHttpClient okHttpClient;
    private static volatile AccessFactory instance = null;

    public static AccessFactory getInstance(Class clazz, ClientConfiguration configuration) {
        if (instance == null) {
            synchronized (AccessFactory.class) {
                if (instance == null) {
                    instance = new AccessFactory(clazz, configuration);
                }
            }
        }
        return instance;
    }

    AccessFactory(Class clazz, ClientConfiguration configuration) {
        this.configuration = configuration;
        buildClient();
        createServices(clazz, okHttpClient);
    }

    private void buildClient() {
        okHttpClient = new OkHttpClient()
                .newBuilder()
                .retryOnConnectionFailure(true)
                .sslSocketFactory(configuration.getSSLFactory(), configuration.getTrustManager())
                .hostnameVerifier( new SSLFactoryUtils.TrustAllHostnameVerifier())
                .connectTimeout(configuration.getConnectTimeOut(), configuration.getTimeUnit())
                .readTimeout(configuration.getReadTimeOut(), configuration.getTimeUnit())
                .writeTimeout(configuration.getWriteTimeOut(), configuration.getTimeUnit())
                .addInterceptor(configuration.getParamsInterceptor())
                .build();
    }

    private void createServices(Class<I> clazz, OkHttpClient okHttpClient) {
        Retrofit builder = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(configuration.getBaseUrl())
                .addConverterFactory(ResponseConvertFactory.create(configuration))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        service = builder.create(clazz);
    }

    public I getService() {
        return service;
    }
}
