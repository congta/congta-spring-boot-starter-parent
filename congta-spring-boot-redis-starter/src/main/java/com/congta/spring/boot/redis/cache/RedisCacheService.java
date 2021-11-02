package com.congta.spring.boot.redis.cache;

import com.congta.spring.boot.shared.cache.CacheService;
import com.congta.spring.boot.shared.ex.CtValidator;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fucheng
 * created in 2021/2/2
 */
public class RedisCacheService implements CacheService {

    private static Logger log = LoggerFactory.getLogger(RedisCacheService.class);

    private RedisFacade redis;

    public RedisCacheService(RedisFacade redis) {
        this.redis = redis;
    }

    @Override
    public void hSet(String key, String field, String value) {
        redis.hset(key, field, value);
    }

    @Override
    public void hSet(String key, String field, int value) {
        redis.hset(key, field, String.valueOf(value));
    }

    @Override
    public Object hGet(String key, String field) {
        CtValidator.notNull(key, "hash key is not exist");
        return redis.hget(key, field);
    }

    @Override
    public String hGetAsString(String key, String field) {
        CtValidator.notNull(key, "hash key is not exist");
        return redis.hget(key, field);
    }

    @Override
    public Integer hGetAsInt(String key, String field) {
        String value = redis.hget(key, field);
        return NumberUtils.isDigits(value) ? Integer.parseInt(value) : null;
    }

    @Override
    public void hDel(String key, String field) {
        CtValidator.notNull(key, "hash key is not exist");
        redis.hdel(key, field);
    }

    @Override
    public void set(String key, String value) {
        redis.set(key, value);
    }

    @Override
    public Object get(String key) {
        return redis.get(key);
    }

    @Override
    public String getAsString(String key) {
        Object value = get(key);
        return value != null ? String.valueOf(value) : null;
    }

    @Override
    public void del(String key) {
        redis.del(key);
    }
}
