package com.congta.spring.boot.web.security;

/**
 * @author Fucheng
 * created in 2021/8/7
 */
public class JwtEntry {

    private String sessionId;
    private String userId;

    public JwtEntry(String sessionId) {
        this.sessionId = sessionId;
    }

    public JwtEntry(String sessionId, String userId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

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
