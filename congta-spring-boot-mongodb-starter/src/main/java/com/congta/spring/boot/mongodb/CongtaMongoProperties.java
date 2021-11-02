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
}
