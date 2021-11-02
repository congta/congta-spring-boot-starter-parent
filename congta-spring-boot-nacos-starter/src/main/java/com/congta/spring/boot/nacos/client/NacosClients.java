package com.congta.spring.boot.nacos.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangfucheng on 2021/11/1.
 */
public class NacosClients {

    private static final Map<NacosEnv, NacosClient> CLIENT_MAP = new ConcurrentHashMap<>();

    public static NacosClient getClient() {
        return getClient(NacosEnv.DEFAULT);
    }

    public static NacosClient getClient(NacosEnv env) {
        return CLIENT_MAP.computeIfAbsent(env, e -> new NacosClient(e, 3000));
    }

}
