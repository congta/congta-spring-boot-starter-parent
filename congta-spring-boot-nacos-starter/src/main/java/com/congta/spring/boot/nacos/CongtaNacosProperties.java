package com.congta.spring.boot.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Fucheng
 * created in 2021/10/16
 */
@ConfigurationProperties(prefix = "spring.nacos")
public class CongtaNacosProperties {

    private String name;
    private String uri;
    private String username;
    private String password;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKcSid() {
        return kcSid;
    }

    public void setKcSid(String kcSid) {
        this.kcSid = kcSid;
    }
}
