package com.congta.spring.boot.etcd;

import java.util.function.Consumer;

/**
 * Created by zhfch on 2022/9/13.
 */
public abstract class CdClient {

    // for local data center
    private static CdClient defaultInstance = null;

    public static CdClient getClient() {
        return defaultInstance;
    }

    public static void setDefault(CdClient local) {
        defaultInstance = local;
    }

    public abstract void getAndWatchString(String path, Consumer<String> consumer);

    public abstract void put(String path, String value);

    public abstract void close();
}
