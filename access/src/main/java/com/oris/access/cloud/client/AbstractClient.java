package com.oris.access.cloud.client;


import com.oris.access.cloud.AccessFactory;

public abstract class AbstractClient<S> {
    private S services;
    private ClientConfiguration configuration;
    protected AbstractClient(Class clazz){
        configuration = initConfiguration();
        initService(clazz);
    }

    protected abstract ClientConfiguration initConfiguration();

    protected void initService(Class clazz){
        services = (S) AccessFactory.getInstance(clazz,configuration).getService();
    }

    public S getService(){
        return services;
    }
}
