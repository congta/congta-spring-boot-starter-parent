package com.congta.spring.boot.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ConfigurationProperties 没有必要加，加上是为了让 idea 自动发现 kc-sid 配置项
 * @author Fucheng
 * created in 2021/10/16
 */
@ConfigurationProperties(
        prefix = "spring.redis"
)
public class CongtaRedisProperties extends RedisProperties {

    private String kcSid;

    public String getKcSid() {
        return kcSid;
    }

    public void setKcSid(String kcSid) {
        this.kcSid = kcSid;
    }
}
