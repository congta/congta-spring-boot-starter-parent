package com.congta.spring.boot.storage.config;


/**
 * 这里面的属性一旦注入 StorageBucket，不会跟着自动刷新，只能重启
 * Created by zhangfucheng on 2021/7/1.
 */
public class StorageBucketConfig {

    /**
     * 提供商，目前只支持 qiniu
     */
    private String provider;
    private String domain;
    private String level;
    private String region;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
