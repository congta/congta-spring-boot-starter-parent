package com.congta.spring.boot.nacos.client;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.congta.spring.boot.nacos.CongtaNacosAutoConfiguration;
import com.congta.spring.boot.nacos.CongtaNacosProperties;
import com.congta.spring.boot.shared.security.KeyCenterFactory;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * 这个类的执行早于 {@link CongtaNacosAutoConfiguration} 晚于 {@link CongtaNacosProperties}
 * Created by zhangfucheng on 2021/11/2.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class NacosValueResolver implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(NacosValueResolver.class);

    @Autowired
    private ConfigurableEnvironment env;
    @Autowired
    private CongtaNacosProperties properties;

    @Override
    public void afterPropertiesSet() {
        inject(env, properties);
    }

    public static void inject(ConfigurableEnvironment env, CongtaNacosProperties properties) {
        if (NacosEnv.DEFAULT == null) {
            if (StringUtils.isNotBlank(properties.getName())) {
                NacosEnv.DEFAULT = NacosEnv.valueOf(properties);
            } else {
                NacosEnv.DEFAULT = NacosEnv.PUBLIC;
            }
        }
        log.info("inject nacos values with NacosEnv.{}", NacosEnv.DEFAULT.name());
        String nacosServerKey = NacosProperties.PREFIX + NacosProperties.SERVER_ADDR;
        if (env.getProperty(nacosServerKey) != null) {
            return;
        }
        NacosEnv nacosEnv = NacosEnv.DEFAULT;
        Properties p = new Properties();
        p.setProperty(nacosServerKey, nacosEnv.uri);
        p.setProperty(NacosProperties.PREFIX + NacosProperties.NAMESPACE, nacosEnv.namespace);
        if (nacosEnv.username != null) {
            String username, password;
            if (StringUtils.isNotBlank(nacosEnv.kcSid)) {
                username = KeyCenterFactory.get(nacosEnv.kcSid).decrypt(nacosEnv.username);
                password = KeyCenterFactory.get(nacosEnv.kcSid).decrypt(nacosEnv.password);
            } else {
                username = nacosEnv.username;
                password = nacosEnv.password;
            }
            p.setProperty(NacosProperties.PREFIX + NacosProperties.USERNAME, username);
            p.setProperty(NacosProperties.PREFIX + NacosProperties.PASSWORD, password);
        }
        MutablePropertySources sources = env.getPropertySources();
        sources.addFirst(new PropertiesPropertySource("nacos", p));
    }
}
