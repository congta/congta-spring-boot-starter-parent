package com.congta.spring.boot.nacos;

import com.congta.spring.boot.nacos.client.NacosValueResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * https://www.baeldung.com/spring-boot-environmentpostprocessor
 * run before InitializingBean
 *
 * 最初发现 jar 包里的 NacosValueResolver 执行非常晚，要晚于 spring-boot-nacos 的自动加载，
 * 所以加了这个类，本地运行没问题，一放到服务器上（差别在于 maven assembler 插件）就不行了，
 * 貌似是 nacos 绑定了 app events，但不知道为什么 idea 运行可以，所以最终这个类没有用到。
 *
 * Created by zhangfucheng on 2021/11/3.
 */
public class CongtaNacosEnvProcessor implements EnvironmentPostProcessor, Ordered {

    private static Logger log = LoggerFactory.getLogger(CongtaNacosEnvProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        CongtaNacosProperties properties = buildPropertiesFromEnvironment(environment);
        log.info("init CongtaNacosEnvProcessor initializing {}", properties.getName());
        NacosValueResolver.inject(environment, properties);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    static CongtaNacosProperties buildPropertiesFromEnvironment(ConfigurableEnvironment environment) {
        CongtaNacosProperties properties = new CongtaNacosProperties();
        properties.setName(environment.getProperty("spring.nacos.name"));
        properties.setNamespace(environment.getProperty("spring.nacos.namespace"));
        properties.setUri(environment.getProperty("spring.nacos.uri"));
        properties.setKcSid(environment.getProperty("spring.nacos.kc-sid"));
        properties.setUsername(environment.getProperty("spring.nacos.username"));
        properties.setPassword(environment.getProperty("spring.nacos.password"));
        return properties;
    }
}
