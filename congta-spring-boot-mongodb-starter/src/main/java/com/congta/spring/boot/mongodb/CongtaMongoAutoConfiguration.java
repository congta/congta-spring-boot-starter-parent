package com.congta.spring.boot.mongodb;

import com.congta.spring.boot.shared.security.KeyCenter;
import com.congta.spring.boot.shared.security.KeyCenterFactory;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Fucheng
 * created in 2021/10/16
 */
@ComponentScan("com.congta.spring.boot.mongodb")
// 之前 mongodb 的包都是 provided scope，现在不会出现 false 了
@ConditionalOnClass({MongoClient.class})
@EnableConfigurationProperties(CongtaMongoProperties.class)
public class CongtaMongoAutoConfiguration {

    @ConditionalOnMissingBean(MongoClient.class)
    @Bean
    public MongoClient mongoClient(CongtaMongoProperties properties) {
        // https://developer.mongodb.com/community/forums/t/sslhandshakeexception-should-not-be-presented-in-certificate-request/12493/2
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
        String host = properties.getUri();
        if (StringUtils.isBlank(properties.getKcSid())) {
            host = host.replace("<username>", properties.getUsername());
            host = host.replace("<password>", properties.getPassword());
        } else {
            KeyCenter keyCenter = KeyCenterFactory.get(properties.getKcSid());
            host = host.replace("<username>", keyCenter.decrypt(properties.getUsername()));
            host = host.replace("<password>", keyCenter.decrypt(properties.getPassword()));
        }
        CongtaMongoProperties.ConnectionPoolProperties poolProperties = properties.getPool() != null
                ? properties.getPool() : new CongtaMongoProperties.ConnectionPoolProperties();
        // return MongoClients.create(host);
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(host))
                .applyToConnectionPoolSettings(builder -> builder
                        .maxSize(poolProperties.getMaxSize())
                        .minSize(poolProperties.getMinSize())
                        .maxWaitTime(poolProperties.getMaxWaitMs(), TimeUnit.MILLISECONDS)
                )
                .build());
    }

    @ConditionalOnMissingBean(MongoDatabase.class)
    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient, CongtaMongoProperties properties) {
        return mongoClient.getDatabase(properties.getDatabase());
    }
}
