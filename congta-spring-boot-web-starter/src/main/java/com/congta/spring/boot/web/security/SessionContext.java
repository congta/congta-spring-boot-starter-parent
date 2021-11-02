package com.congta.spring.boot.web.security;

import com.congta.spring.boot.shared.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 保存当此请求的信息，响应完成之后被清除
 * @author Fucheng
 * created in 2021/2/18
 */
@Component
public class SessionContext {

    private static Logger log = LoggerFactory.getLogger(SessionContext.class);

    @Autowired
    private CacheService cacheService;

    private ThreadLocal<SessionMeta> valueHolder = new ThreadLocal<>();

    public SessionMeta getOrNull() {
        return valueHolder.get();
    }

    // non thread safe
    public SessionMeta create(String sessionId) {
        SessionMeta ctx = valueHolder.get();
        if (ctx != null) {
            log.warn("a session is exist in current context");
        }
        ctx = new SessionMeta();
        ctx.setSessionId(sessionId);
        valueHolder.set(ctx);
        return ctx;
    }

    public void clear() {
        valueHolder.remove();
    }

    public String getUserId() {
        return getOrNull().getUserId();
    }

    public String getSessionId() {
        return getOrNull().getUserId();
    }

    /* ============== session ================ */

    public void setSession(String field, String value) {
        // 过了 LoginInterceptor 就一定会有 session
        String sessionId = getOrNull().getSessionId();
        cacheService.hSet(sessionId, field, value);
        log.info("save session for id: {}, key: {}, value: {}", sessionId, field, value);
    }

    public void setSession(String field, int value) {
        // 过了 LoginInterceptor 就一定会有 session
        String sessionId = getOrNull().getSessionId();
        cacheService.hSet(sessionId, field, value);
        log.info("save session for id: {}, key: {}, value: {}", sessionId, field, value);
    }

    public String getSessionAsString(String field) {
        // 过了 LoginInterceptor 就一定会有 session
        String sessionId = getOrNull().getSessionId();
        String value = cacheService.hGetAsString(sessionId, field);
        log.info("get session for id: {}, key: {}, value: {}", sessionId, field, value);
        return value;
    }

    public int getSessionAsInt(String field, int defaultValue) {
        // 过了 LoginInterceptor 就一定会有 session
        String sessionId = getOrNull().getSessionId();
        Integer value = cacheService.hGetAsInt(sessionId, field);
        log.info("get session for id: {}, key: {}, value: {}", sessionId, field, value);
        return value != null ? value : defaultValue;
    }
}
