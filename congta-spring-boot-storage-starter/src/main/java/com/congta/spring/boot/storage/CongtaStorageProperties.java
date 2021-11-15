package com.congta.spring.boot.storage;

import com.congta.spring.boot.shared.config.SharedAuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ConfigurationProperties 没有必要加，加上是为了让 idea 自动发现 kc-sid 配置项
 * @author Fucheng
 * created in 2021/10/16
 */
@ConfigurationProperties(
        prefix = "spring.storage"
)
public class CongtaStorageProperties {

    private String kcSid;

    private String configId;

    private AuthConfig qiniu = new AuthConfig();

    public String getKcSid() {
        return kcSid;
    }

    public void setKcSid(String kcSid) {
        this.kcSid = kcSid;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public AuthConfig getQiniu() {
        return qiniu;
    }

    public void setQiniu(AuthConfig qiniu) {
        this.qiniu = qiniu;
    }

    // 好像只有内部类才能被 spring-boot-configuration-processor 处理
    public static class AuthConfig extends SharedAuthConfig {
    }
}
