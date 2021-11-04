package com.congta.spring.boot.nacos;

import com.congta.spring.boot.nacos.client.NacosValueResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 本地运行与 sh 启动，组件加载顺序不一致， sh 启动的最终顺序是：
 * 1. onApplicationEvent org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
 * 2. ApplicationContextInitializer
 * 3. onApplicationEvent org.springframework.boot.context.event.ApplicationContextInitializedEvent
 * 4. onApplicationEvent org.springframework.boot.context.event.ApplicationPreparedEvent
 * 5. mongodb 加载
 * 6. nacos 加载
 * 没有查到怎么控制，等请教大神
 *
 * 所以 nacos 需要环境变量的注入在 1-4 步都是可以的，目前使用第 4 步，本类保留但是没用到
 *
 * Created by zhangfucheng on 2021/11/3.
 */
public class CongtaNacosContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static Logger log = LoggerFactory.getLogger(CongtaNacosContextInitializer.class);

    private ConfigurableEnvironment env;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        env = applicationContext.getEnvironment();
        CongtaNacosProperties properties = CongtaNacosEnvProcessor.buildPropertiesFromEnvironment(env);
        log.warn("CongtaNacosContextInitializer run {}", properties.getName());
        NacosValueResolver.inject(env, properties);
    }
}
