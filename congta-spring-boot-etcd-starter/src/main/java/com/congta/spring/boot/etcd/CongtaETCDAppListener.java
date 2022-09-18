package com.congta.spring.boot.etcd;

import com.congta.spring.boot.shared.security.KeyCenter;
import com.congta.spring.boot.shared.security.KeyCenterFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Created by zhfch on 2022/9/12.
 */
public class CongtaETCDAppListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger log = LoggerFactory.getLogger(CongtaETCDAppListener.class);

    public void processEnvironment(ConfigurableEnvironment environment) {
        String name = environment.getProperty("spring.etcd.name");
        log.warn("load etcd by name {}", name);
        CdEnv env = CdEnv.valueOf(name);
        if (env != null) {
            CdClient.setDefault(create(env.config));
        } else {
            throw new RuntimeException("unknown etcd env name: " + name);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationPreparedEvent) {
            ConfigurableApplicationContext appCtx = ((ApplicationPreparedEvent)event).getApplicationContext();
            ConfigurableEnvironment env = appCtx.getEnvironment();
            processEnvironment(env);
        } else {
            log.debug("onApplicationEvent {}", event.getClass().getSimpleName());
        }
    }

    private CdClientImpl create(ETCDConfig etcdConfig) {
        byte[] username = Base64.decodeBase64(etcdConfig.getUsername());
        byte[] password = Base64.decodeBase64(etcdConfig.getPassword());
        if (StringUtils.isNotBlank(etcdConfig.getKcSid())) {
            KeyCenter keyCenter = KeyCenterFactory.get(etcdConfig.getKcSid());
            username = keyCenter.decrypt(username);
            password = keyCenter.decrypt(password);
        }

        return new CdClientImpl(
                etcdConfig.getUri(),
                username,
                password,
                etcdConfig.getPathPrefix()
        );
    }
}
