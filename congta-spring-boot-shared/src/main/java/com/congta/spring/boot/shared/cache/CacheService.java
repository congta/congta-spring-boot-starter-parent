package com.congta.spring.boot.shared.cache;

/**
 * 缓存服务，可选单机或分布式实现，接口仿照 redis
 * @author Fucheng
 * created in 2021/2/2
 */
public interface CacheService {

    void hSet(String key, String field, String value);

    void hSet(String key, String field, int value);

    Object hGet(String key, String field);

    String hGetAsString(String key, String field);

    Integer hGetAsInt(String key, String field);

    void hDel(String key, String field);

    void set(String key, String value);

    Object get(String key);

    String getAsString(String key);

    void del(String key);
}
