package com.congta.spring.boot.redis;

import com.congta.spring.boot.redis.cache.RedisFacade;
import com.congta.spring.boot.redis.cache.RedisPoolFacade;
import com.congta.spring.boot.shared.security.KeyCenter;
import com.congta.spring.boot.shared.security.KeyCenterFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Fucheng
 * created in 2021/10/16
 */
@ComponentScan("com.congta.spring.boot.redis")
@EnableConfigurationProperties(CongtaRedisProperties.class)
public class CongtaRedisAutoConfiguration {

    @ConditionalOnMissingBean(RedisFacade.class)
    @Bean
    public RedisFacade redisFacade(CongtaRedisProperties properties) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(properties.getJedis().getPool().getMaxActive());
        poolConfig.setMaxIdle(properties.getJedis().getPool().getMaxIdle());
        poolConfig.setMaxWaitMillis(properties.getJedis().getPool().getMaxWait().toMillis());

        JedisPool pool;
        if (StringUtils.isBlank(properties.getKcSid())) {
            pool = new JedisPool(poolConfig, properties.getHost(), properties.getPort(),
                    JedisCluster.DEFAULT_TIMEOUT, properties.getPassword());
        } else {
            KeyCenter keyCenter = KeyCenterFactory.get(properties.getKcSid());
            pool = new JedisPool(poolConfig, properties.getHost(), properties.getPort(),
                    JedisCluster.DEFAULT_TIMEOUT, keyCenter.decrypt(properties.getPassword()));
        }

        return new RedisPoolFacade(pool);
    }
}
