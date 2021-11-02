package com.congta.spring.boot.shared.cache;

import com.congta.spring.boot.shared.ex.CtValidator;
import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Fucheng
 * created in 2021/2/2
 */
public class MemoryCacheService implements CacheService {

    private static Logger log = LoggerFactory.getLogger(MemoryCacheService.class);

    private LoadingCache<String, Map<String, Object>> session = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(CacheLoader.from((Supplier<Map<String, Object>>) HashMap::new));
    private Cache<String, Object> kvCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    public void hSetObj(String key, String field, Object value) {
        CtValidator.notNull(key, "hash key is not exist");
        try {
            session.get(key).put(field, value);
        } catch (ExecutionException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "set hash error", e);
        }
    }

    @Override
    public void hSet(String key, String field, String value) {
        hSetObj(key, field, value);
    }

    @Override
    public void hSet(String key, String field, int value) {
        hSetObj(key, field, value);
    }

    @Override
    public Object hGet(String key, String field) {
        CtValidator.notNull(key, "hash key is not exist");
        try {
            return getObject(session.get(key), field);
        } catch (ExecutionException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get hash error", e);
        }
    }

    @Override
    public String hGetAsString(String key, String field) {
        CtValidator.notNull(key, "hash key is not exist");
        try {
            return getString(session.get(key), field);
        } catch (ExecutionException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get hash as string error", e);
        }
    }

    @Override
    public Integer hGetAsInt(String key, String field) {
        String value = hGetAsString(key, field);
        return StringUtils.hasText(value) ? Integer.parseInt(value) : null;
    }

    @Override
    public void hDel(String key, String field) {
        CtValidator.notNull(key, "hash key is not exist");
        try {
            session.get(key).remove(field);
        } catch (ExecutionException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get hash as string error", e);
        }
    }

    @Override
    public void set(String key, String value) {
        kvCache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return kvCache.getIfPresent(key);
    }

    @Override
    public String getAsString(String key) {
        Object value = get(key);
        return value != null ? String.valueOf(value) : null;
    }

    @Override
    public void del(String key) {
        kvCache.invalidate(key);
    }

    /**
     * copied from commons-collections4
     */
    public static <K, V> V getObject(final Map<? super K, V> map, final K key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    /**
     * copied from commons-collections4
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }
}
