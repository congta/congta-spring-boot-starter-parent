package com.congta.spring.boot.mongodb;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Fucheng
 * created in 2021/10/16
 */
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class CongtaMongoProperties {

    private String uri;
    private String username;
    private String password;
    private String database;
    private String kcSid;
    private ConnectionPoolProperties pool;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getKcSid() {
        return kcSid;
    }

    public void setKcSid(String kcSid) {
        this.kcSid = kcSid;
    }

    public ConnectionPoolProperties getPool() {
        return pool;
    }

    public void setPool(ConnectionPoolProperties pool) {
        this.pool = pool;
    }

    static class ConnectionPoolProperties {
        private int maxSize = 100;
        private int minSize;
        private long maxWaitMs = 1000 * 60 * 2;

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getMinSize() {
            return minSize;
        }

        public void setMinSize(int minSize) {
            this.minSize = minSize;
        }

        public long getMaxWaitMs() {
            return maxWaitMs;
        }

        public void setMaxWaitMs(long maxWaitMs) {
            this.maxWaitMs = maxWaitMs;
        }
    }
}
