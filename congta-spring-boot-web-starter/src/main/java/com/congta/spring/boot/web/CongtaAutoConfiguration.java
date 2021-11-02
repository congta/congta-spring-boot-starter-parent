package com.congta.spring.boot.web;

import com.congta.spring.boot.shared.cache.CacheService;
import com.congta.spring.boot.shared.cache.MemoryCacheService;
import com.congta.spring.boot.shared.security.KeyCenter;
import com.congta.spring.boot.shared.security.KeyCenterFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by zhangfucheng on 2021/10/15.
 */
@ComponentScan("com.congta.spring.boot.web")
public class CongtaAutoConfiguration {

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JsonOrgModule())
                .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @ConditionalOnMissingBean(CacheService.class)
    @Bean
    public CacheService cacheService() {
        return new MemoryCacheService();
    }

    @ConditionalOnProperty("app.kc.sid")
    @ConditionalOnMissingBean(KeyCenter.class)
    @Bean
    public KeyCenter keyCenter(@Value("${app.kc.sid}") String sid) {
        return KeyCenterFactory.get(sid);
    }

}
