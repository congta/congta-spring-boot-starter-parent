package com.congta.spring.boot.nacos;

import com.congta.spring.boot.nacos.client.NacosClient;
import com.congta.spring.boot.nacos.client.NacosClients;
import com.congta.spring.boot.nacos.client.NacosEnv;
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
@ComponentScan("com.congta.spring.boot.nacos")
@ConditionalOnClass({NacosClient.class})
@EnableConfigurationProperties(CongtaNacosProperties.class)
public class CongtaNacosAutoConfiguration {

    @ConditionalOnMissingBean(NacosClient.class)
    @Bean
    public NacosClient nacosClient(CongtaNacosProperties properties) {
        if (StringUtils.isNotBlank(properties.getName())) {
            NacosEnv.DEFAULT = NacosEnv.valueOf(properties);
        }
        return NacosClients.getClient();
    }

}
