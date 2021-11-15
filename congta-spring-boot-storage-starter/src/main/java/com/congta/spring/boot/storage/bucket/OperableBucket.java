package com.congta.spring.boot.storage.bucket;

/**
 * @author Fucheng
 * created in 2021/11/13
 */
public interface OperableBucket {

    String upload(byte[] file, String ns, String key);

    boolean exist(String key);

    String getUrl(String key, int width, int height);

    default String getUrl(String key) {
        return getUrl(key, -1, -1);
    }
}
