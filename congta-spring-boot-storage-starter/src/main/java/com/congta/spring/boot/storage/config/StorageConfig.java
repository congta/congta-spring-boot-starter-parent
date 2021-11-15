package com.congta.spring.boot.storage.config;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 存储在 https://gitee.com/zeplios/spring-config-repo/tree/master/config-root
 * Created by zhangfucheng on 2021/7/1.
 */
@Component
// 如果用 NacosConfigurationProperties，好像做不到 onChange 之后重新加载 StorageService
// 所以改一个名字，用 hocon 格式手动加载
//@NacosConfigurationProperties(dataId = "srv_storage",
//        groupId = "maptime",
//        prefix = "storage",
//        autoRefreshed = true,
//        type = ConfigType.YAML
//)
public class StorageConfig {

    private Map<String, StorageNsConfig> namespaces;

    private Map<String, StorageBucketConfig> buckets;

    public Map<String, StorageNsConfig> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, StorageNsConfig> namespaces) {
        this.namespaces = namespaces;
    }

    public Map<String, StorageBucketConfig> getBuckets() {
        return buckets;
    }

    public void setBuckets(Map<String, StorageBucketConfig> buckets) {
        this.buckets = buckets;
    }
}
