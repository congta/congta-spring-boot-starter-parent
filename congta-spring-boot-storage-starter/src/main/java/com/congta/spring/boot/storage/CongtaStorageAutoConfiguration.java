package com.congta.spring.boot.storage;

import com.congta.spring.boot.etcd.CdClient;
import com.congta.spring.boot.shared.security.KeyCenter;
import com.congta.spring.boot.shared.security.KeyCenterFactory;
import com.congta.spring.boot.shared.security.NoopKeyCenter;
import com.congta.spring.boot.shared.util.YamlMapper;
import com.congta.spring.boot.storage.config.StorageConfig;
import com.qiniu.util.Auth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Fucheng
 * created in 2021/10/16
 */
@ComponentScan("com.congta.spring.boot.storage")
@EnableConfigurationProperties(CongtaStorageProperties.class)
public class CongtaStorageAutoConfiguration {

    @Value("${app.kc-sid}")
    private String appKcSid;

    @ConditionalOnMissingBean(StorageService.class)
    @Bean
    public StorageService storageService(CongtaStorageProperties properties) {
        String kcSid = StringUtils.defaultIfBlank(properties.getKcSid(), appKcSid);
        KeyCenter keyCenter = StringUtils.isNotBlank(kcSid)
                ? KeyCenterFactory.get(kcSid)
                : NoopKeyCenter.INSTANCE;

        // qiniu
        Auth authQiNiu = null;
        if (properties.getQiniu() != null && StringUtils.isNotBlank(properties.getQiniu().getAk())) {
            authQiNiu = Auth.create(
                    keyCenter.decrypt(properties.getQiniu().getAk()),
                    keyCenter.decrypt(properties.getQiniu().getSk())
            );
        }

        String configId = StringUtils.defaultIfBlank(properties.getConfigId(), "/srv_storage");
        StorageService storageService = new StorageService(authQiNiu, null);

        // https://www.baeldung.com/jackson-yaml
        CdClient.getClient().getAndWatchString(configId, data -> {
            storageService.updateConfig(YamlMapper.readValue(data, StorageConfig.class));
        });

        return storageService;
    }
}
