package com.congta.spring.boot.web.security;

/**
 * @author Fucheng
 * created in 2021/2/24
 */
public class SessionMeta {

    private String userId;
    private String sessionId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
