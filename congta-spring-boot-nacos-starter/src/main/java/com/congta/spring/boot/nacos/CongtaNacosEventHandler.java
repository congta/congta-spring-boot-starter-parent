package com.congta.spring.boot.nacos;

import com.congta.spring.boot.nacos.client.NacosValueResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * https://stackoverflow.com/questions/58725479/spring-boot-2-do-something-before-the-beans-are-initialized
 * Created by zhangfucheng on 2021/11/3.
 */
public class CongtaNacosEventHandler implements ApplicationListener<ApplicationEvent> {

    private static Logger log = LoggerFactory.getLogger(CongtaNacosContextInitializer.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationPreparedEvent) {
            ConfigurableApplicationContext appCtx = ((ApplicationPreparedEvent) event).getApplicationContext();
            ConfigurableEnvironment env = appCtx.getEnvironment();
            CongtaNacosProperties properties = CongtaNacosEnvProcessor.buildPropertiesFromEnvironment(env);
            // 这里仍然不能用 appCtx.getBean 来获取 properties
            // java.lang.IllegalStateException: org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@6d7fc27 has not been refreshed yet
            //	at org.springframework.context.support.AbstractApplicationContext.assertBeanFactoryActive(AbstractApplicationContext.java:1146)
            log.warn("onApplicationEvent ApplicationPreparedEvent {}", properties.getName());
            NacosValueResolver.inject(env, properties);
            return;
        }
        log.debug("onApplicationEvent {}", event.getClass().getSimpleName());
    }
}
